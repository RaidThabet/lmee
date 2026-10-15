resource "azurerm_storage_account" "main" {
  account_replication_type        = "LRS"
  account_tier                    = "Standard"
  location                        = azurerm_resource_group.rg.location
  name                            = "clubsstorage"
  resource_group_name             = azurerm_resource_group.rg.name
  allow_nested_items_to_be_public = true
}

resource "azurerm_storage_container" "crests" {
  name                  = "crests"
  storage_account_id    = azurerm_storage_account.main.id
  container_access_type = "blob"
}

resource "azurerm_storage_blob" "default_crest" {
  name                 = "default-crest.png"
  type                 = "Block"
  storage_container_id = azurerm_storage_container.crests.id
  source               = "default-crest.png"
}

resource "azurerm_user_assigned_identity" "storage" {
  name                = "id-api-storage"
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
}

resource "azurerm_role_assignment" "crest_storage" {
  scope                = azurerm_storage_account.main.id
  role_definition_name = "Storage Blob Data Contributor"
  principal_id         = azurerm_user_assigned_identity.storage.principal_id
}

resource "azurerm_storage_management_policy" "crests" {
  storage_account_id = azurerm_storage_account.main.id

  rule {
    enabled = true
    name    = "delete-temp-crests"
    filters {
      prefix_match = ["crests/temp/"]
      blob_types   = ["blockBlob"]
    }
    actions {
      base_blob {
        delete_after_days_since_modification_greater_than = 1
      }
    }
  }
}