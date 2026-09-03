output "backend_url" {
  description = "Public base URL of the backend container app"
  value       = "https://${azurerm_container_app.backend.ingress[0].fqdn}"
}

output "keycloak_url" {
  description = "Public base URL of Keycloak"
  value       = local.keycloak_url
}

output "keycloak_issuer_uri" {
  description = "Issuer the backend validates the `iss` claim against; also what a frontend points its OIDC client at"
  value       = "${local.keycloak_url}/realms/${var.keycloak_realm}"
}

output "keycloak_admin_console_url" {
  description = "Keycloak master-realm admin console, for creating the app realm and its clients"
  value       = "${local.keycloak_url}/admin"
}

output "acr_login_server" {
  description = "Registry hostname to tag and push backend images to"
  value       = azurerm_container_registry.acr.login_server
}

output "resource_group_name" {
  description = "Resource group holding every resource in this stack"
  value       = azurerm_resource_group.rg.name
}

output "backend_app_name" {
  description = "Container app name to target when rolling out a new backend image"
  value       = azurerm_container_app.backend.name
}

output "postgres_fqdn" {
  description = "Private FQDN of the PostgreSQL flexible server"
  value       = azurerm_postgresql_flexible_server.lmeePostgres.fqdn
}

output "lmee_database_name" {
  description = "Application database on the flexible server"
  value       = azurerm_postgresql_flexible_server_database.lmee.name
}
