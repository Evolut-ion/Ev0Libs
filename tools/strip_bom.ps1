$p = "src\main\java\com\Ev0sMods\PhosphorTech\blocks\MechanicalGrinderState.java"
$b = [System.IO.File]::ReadAllBytes($p)
if ($b.Length -ge 3 -and $b[0] -eq 0xEF -and $b[1] -eq 0xBB -and $b[2] -eq 0xBF) {
    [System.IO.File]::WriteAllBytes($p, $b[3..($b.Length-1)])
    Write-Output "BOM removed"
} else {
    Write-Output "No BOM found"
}