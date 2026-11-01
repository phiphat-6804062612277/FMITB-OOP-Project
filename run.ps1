Write-Host "===================================================" -ForegroundColor Cyan
Write-Host "  Building and Running Five Minutes In The Bathroom" -ForegroundColor Yellow
Write-Host "===================================================" -ForegroundColor Cyan

if (!(Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

$sources = Get-ChildItem -Path "src" -Filter "*.java" -Recurse | ForEach-Object { $_.FullName }
if ($sources.Count -eq 0) {
    Write-Host "No Java files found to compile!" -ForegroundColor Red
    exit 1
}

$sources | Out-File -FilePath "sources.txt" -Encoding utf8
Write-Host "Compiling $($sources.Count) Java source files..." -ForegroundColor Green
javac -encoding UTF-8 -d bin "@sources.txt"

if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed with error code $LASTEXITCODE" -ForegroundColor Red
    Remove-Item "sources.txt" -ErrorAction SilentlyContinue
    exit $LASTEXITCODE
}
Remove-Item "sources.txt" -ErrorAction SilentlyContinue

Write-Host "Starting Five Minutes In The Bathroom..." -ForegroundColor Green
java -cp bin Main
