locals {
  keycloak_app_name = "keycloak"
  keycloak_url = "https://${local.keycloak_app_name}.${azurerm_container_app_environment.example.default_domain}"

  postgres_host = azurerm_postgresql_flexible_server.lmeePostgres.fqdn
}

resource "azurerm_container_app_environment" "example" {
  name                       = "Example-Environment"
  location                   = azurerm_resource_group.rg.location
  resource_group_name        = azurerm_resource_group.rg.name
  log_analytics_workspace_id = azurerm_log_analytics_workspace.lmeeaw.id

  infrastructure_subnet_id = azurerm_subnet.container_apps.id

  workload_profile {
    name                  = "Consumption"
    workload_profile_type = "Consumption"
  }
}

resource "azurerm_container_app" "backend" {
  name                         = "backend"
  container_app_environment_id = azurerm_container_app_environment.example.id
  resource_group_name          = azurerm_resource_group.rg.name
  revision_mode                = "Single"
  workload_profile_name        = "Consumption"

  depends_on = [azurerm_role_assignment.acr_pull]

  identity {
    type         = "UserAssigned"
    identity_ids = [azurerm_user_assigned_identity.acr_pull.id]
  }

  registry {
    server   = azurerm_container_registry.acr.login_server
    identity = azurerm_user_assigned_identity.acr_pull.id
  }

  secret {
    name  = "lmee-postgres-password"
    value = var.lmee_db_password
  }

  template {
    min_replicas = 1
    max_replicas = 3

    container {
      name  = "lmee-backend"
      image = "mcr.microsoft.com/k8se/samples/test-app:latest"

      cpu    = 0.5
      memory = "1Gi"

      env {
        name  = "SPRING_PROFILES_ACTIVE"
        value = "prod"
      }

      env {
        name  = "LMEE_POSTGRES_DB"
        value = "jdbc:postgresql://${local.postgres_host}:5432/${azurerm_postgresql_flexible_server_database.lmee.name}?sslmode=require"
      }

      env {
        name  = "LMEE_POSTGRES_USER"
        value = var.lmee_db_username
      }

      env {
        name        = "LMEE_POSTGRES_PASSWORD"
        secret_name = "lmee-postgres-password"
      }

      env {
        name  = "KEYCLOAK_ISSUER_URI"
        value = "${local.keycloak_url}/realms/${var.keycloak_realm}"
      }
    }
  }

  ingress {
    external_enabled = true
    target_port      = 8080

    traffic_weight {
      percentage      = 100
      latest_revision = true
    }
  }

  lifecycle {
    # image tag is owned by .github/workflows/deploy.yml.
    ignore_changes = [template[0].container[0].image]
  }
}

resource "azurerm_container_app" "keycloak" {
  name                         = local.keycloak_app_name
  container_app_environment_id = azurerm_container_app_environment.example.id
  resource_group_name          = azurerm_resource_group.rg.name
  revision_mode                = "Single"
  workload_profile_name        = "Consumption"

  secret {
    name  = "keycloak-db-password"
    value = var.lmee_db_password
  }

  secret {
    name  = "keycloak-admin-password"
    value = var.keycloak_admin_password
  }

  template {
    min_replicas = 1
    max_replicas = 1

    container {
      name   = "keycloak"
      image  = "quay.io/keycloak/keycloak:26.7.2"
      cpu    = 0.5
      memory = "1Gi"

      command = ["/opt/keycloak/bin/kc.sh", "start"]

      env {
        name  = "KC_DB"
        value = "postgres"
      }

      env {
        name  = "KC_DB_URL"
        value = "jdbc:postgresql://${local.postgres_host}:5432/${azurerm_postgresql_flexible_server_database.keycloak.name}?sslmode=require"
      }

      env {
        name  = "KC_DB_USERNAME"
        value = var.lmee_db_username
      }

      env {
        name        = "KC_DB_PASSWORD"
        secret_name = "keycloak-db-password"
      }

      env {
        name  = "KC_HOSTNAME"
        value = local.keycloak_url
      }

      env {
        name  = "KC_HOSTNAME_BACKCHANNEL_DYNAMIC"
        value = "true"
      }

      env {
        name  = "KC_HTTP_ENABLED"
        value = "true"
      }

      env {
        name  = "KC_PROXY_HEADERS"
        value = "xforwarded"
      }

      env {
        name  = "KC_HEALTH_ENABLED"
        value = "true"
      }

      env {
        name  = "KC_BOOTSTRAP_ADMIN_USERNAME"
        value = var.keycloak_admin_username
      }

      env {
        name        = "KC_BOOTSTRAP_ADMIN_PASSWORD"
        secret_name = "keycloak-admin-password"
      }

      startup_probe {
        transport = "HTTP"
        port      = 9000
        path      = "/health/started"
      }

      readiness_probe {
        transport = "HTTP"
        port      = 9000
        path      = "/health/ready"
      }

      liveness_probe {
        transport = "HTTP"
        port      = 9000
        path      = "/health/live"
      }
    }
  }

  ingress {
    external_enabled = true
    target_port      = 8080

    traffic_weight {
      percentage      = 100
      latest_revision = true
    }
  }
}
