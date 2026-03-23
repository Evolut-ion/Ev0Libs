StateData → Component Map Migration

Goal
- Replace direct-storage-in-`StateData` fields with a stable component-based map attached to `BlockType` or a related stable object.
- Maintain backward compatibility during migration and allow prerelease servers (where `StateData` fields may be removed) to keep working.

High-level approach
1. Introduce a stable `ComponentMap` API (map of String -> Object) that can be attached to `BlockType` (or another stable host).
2. Provide helpers to read/write values from either the new `ComponentMap` or old `StateData` fields (reflection + synthetic fallback).
3. Migrate code in phases: write new values to `ComponentMap` while continuing to read from both sources until all mods use the `ComponentMap`.
4. Remove `StateData` dependency once ecosystem migrated.

Data model
- ComponentMap: a lightweight map-like component keyed by strings (e.g. `ev0s:chisel`) that stores POJOs or primitive wrappers.
- Attach to `BlockType` using a stable API (either via an official `addComponent` method if available, or via a reflective `components` field on `BlockType`).
- Internally, use a ConcurrentHashMap<String,Object> for thread-safety and performance.

APIs (suggested)
- ComponentMap getOrCreateComponent(BlockType bt, String key)
- Object componentGet(ComponentMap cm, String field)
- void componentPut(ComponentMap cm, String field, Object value)
- Optional: typed helpers `T componentGetAs(ComponentMap cm, String field, Class<T> cls)`

Backward-compatibility helpers
- `ReflectionCache.getFieldValue(clazz, target, fieldName)` (already added) reads real field or synthetic fallback.
- `ReflectionCache.setField(clazz, target, fieldName, value)` now preserves values in `SyntheticFieldStore` if field missing.
- Provide `CompatAdapters` with methods:
  - `Object readStateFieldOrComponent(BlockType bt, String stateField, String componentKey, String componentField)`
  - `void writeStateFieldAndComponent(BlockType bt, String stateField, String componentKey, String componentField, Object value)`
  This writes into `ComponentMap` and also attempts to set the old `StateData` field (or synthetic store) so older readers continue to work.

Migration steps (practical)
1. Add `ComponentMap` implementation and attach helpers to your mod runtime.
2. Update writers: whenever code sets fields on `StateData` (e.g. `id`, `source`, `substitutions`), instead call the adapter that writes both the `ComponentMap` and the `StateData` via `ReflectionCache.setField`.
   - Example:
     ```java
     ComponentMap cm = ComponentHelper.getOrCreate(bt, "ev0s:chisel");
     ComponentHelper.put(cm, "source", source);
     ReflectionCache.setField(StateData.class, data, "id", "Ev0sChisel"); // preserved synthetically if removed
     ```
3. Update readers: replace direct casts/field access with calls that try `ComponentMap` first, then fall back to `ReflectionCache.getFieldValue`.
   - Example:
     ```java
     Object v = ComponentHelper.get(cm, "source");
     if (v == null) v = ReflectionCache.getFieldValue(StateData.class, data, "id");
     ```
4. Iterate across all mods: convert writers, then readers. When no code writes to `StateData` fields anymore and tests pass, remove synthetic fallbacks.

Serialization & Codec
- If you previously relied on `StateData` subclass codecs, implement codecs for data stored inside `ComponentMap` (e.g., store a JSON/codec-friendly representation inside the component map). Keep reading compatibility by preserving `StateData` values synthetically until everyone migrates.

Testing checklist
- Unit tests for `ComponentHelper` read/write semantics.
- Integration test: spawn prerelease server, verify that values written to `ComponentMap` are visible via new reader paths and that older reader code still observes values (via `ReflectionCache` synthetic store).
- Performance test: measure impact of map lookups; cache ComponentMap references to avoid repeated lookups if hot.

Rollout plan
1. Implement `ComponentMap` and `CompatAdapters` in this repo; keep `ReflectionCache` synthetic fallback enabled.
2. Update this mod’s writers → ComponentMap + setField; update readers to prefer ComponentMap.
3. Publish guidance and small library (ComponentHelper + ReflectionCache) for other mod authors.
4. Coordinate with other mod authors to migrate their writers/readers.
5. After all mods migrated (and no synthetic writes are used), remove `SyntheticFieldStore` and synthetic fallback.

Notes & tradeoffs
- SyntheticFieldStore: convenient transitional shim but should be temporary; it stores values per-instance and may diverge from real `StateData` if both are updated separately. Use ComponentMap as source-of-truth.
- Prefer storing simple POJOs or codec-friendly types in `ComponentMap` to make persistence simpler.
- Keep keys namespaced (e.g. `ev0s:chisel`) to avoid collisions.

Example helper (pseudo)
- `ComponentHelper.getOrCreate(bt, key)` — returns a map-like component
- `ComponentHelper.put(bt, key, field, value)` — convenience to attach value in one call

If you want, I can:
- Add a `ComponentMap` implementation and `ComponentHelper` class in this repo and convert one example writer/reader to demonstrate the migration.
- Produce a small library package (single-file) other modders can copy.

