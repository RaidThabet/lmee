terraform {
  backend "azurerm" {
    resource_group_name  = "tfstate-lmee"
    storage_account_name = "lmeesa"
    container_name       = "tfstate"
    key                  = "demo.terraform.tfstate"
  }
}