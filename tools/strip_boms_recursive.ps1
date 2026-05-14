$root = Get-Location
$paths = @('src', '.')
foreach ($p in $paths) {
    if (-Not (Test-Path $p)) { continue }
    Get-ChildItem -Path $p -Recurse -File -Include *.java,*.xml,*.json,*.md,*.properties,*.gradle,*.bat,*.ps1 | ForEach-Object {
        $f = $_.FullName
        try {
            $b = [System.IO.File]::ReadAllBytes($f)
            if ($b.Length -ge 3 -and $b[0] -eq 0xEF -and $b[1] -eq 0xBB -and $b[2] -eq 0xBF) {
                [System.IO.File]::WriteAllBytes($f, $b[3..($b.Length-1)])
                Write-Output "Removed BOM: $f"
            }
        } catch {
            Write-Output "Failed: $f -> $_"
        }
    }
}
Write-Output 'Done'