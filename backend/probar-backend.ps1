$ErrorActionPreference = "Stop"
$producto = @{
    codigo = "K-" + (Get-Random -Maximum 999999)
    nombre = "Polera Kaiju"
    descripcion = "Producto de prueba"
    categoria = "Ropa"
    precio = 15000
    stock = 10
    stockMinimo = 3
} | ConvertTo-Json

Write-Host "1. Creando producto"
$creado = Invoke-RestMethod -Uri "http://localhost:8081/api/productos" -Method Post -ContentType "application/json" -Body $producto
$creado | Format-List

Write-Host "2. Registrando entrada"
$entrada = @{productoId = $creado.id; tipo = "ENTRADA"; cantidad = 5} | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8082/api/movimientos" -Method Post -ContentType "application/json" -Body $entrada | Format-List

Write-Host "3. Registrando venta"
$venta = @{productoId = $creado.id; cantidad = 2} | ConvertTo-Json
Invoke-RestMethod -Uri "http://localhost:8083/api/ventas" -Method Post -ContentType "application/json" -Body $venta | Format-List

Write-Host "4. Comprobando stock final: esperado 13"
$final = Invoke-RestMethod -Uri "http://localhost:8081/api/productos/$($creado.id)"
if ($final.stock -ne 13) { throw "Stock final incorrecto: $($final.stock)" }
Write-Host "Prueba correcta. Stock final: $($final.stock)" -ForegroundColor Green
