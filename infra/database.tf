resource "azurerm_postgresql_flexible_server" "lmeePostgres" {
  name                          = "lmee-db"
  resource_group_name           = azurerm_resource_group.rg.name
  location                      = azurerm_resource_group.rg.location
  version                       = "18"
  delegated_subnet_id           = azurerm_subnet.subnet.id
  private_dns_zone_id           = azurerm_private_dns_zone.lmeeDBPrivateDNS.id
  public_network_access_enabled = false
  administrator_login           = var.lmee_db_username
  administrator_password        = var.lmee_db_password
  zone                          = "1"

  storage_mb   = 32768
  storage_tier = "P4"

  sku_name   = "B_Standard_B1ms"
  depends_on = [azurerm_private_dns_zone_virtual_network_link.lmeeDBPrivateDNSNetworkLink]
}

resource "azurerm_postgresql_flexible_server_database" "lmee" {
  name      = var.lmee_db_name
  server_id = azurerm_postgresql_flexible_server.lmeePostgres.id
  collation = "en_US.utf8"
  charset   = "UTF8"

  lifecycle {
    prevent_destroy = true
  }
}

resource "azurerm_postgresql_flexible_server_database" "keycloak" {
  name      = var.keycloak_db_name
  server_id = azurerm_postgresql_flexible_server.lmeePostgres.id
  collation = "en_US.utf8"
  charset   = "UTF8"

  lifecycle {
    prevent_destroy = true
  }
}

