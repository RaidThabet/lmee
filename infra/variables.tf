variable "lmee_db_username" {
  description = "Administrator login for the LMEE PostgreSQL flexible server"
  type        = string
}

variable "lmee_db_password" {
  description = "Administrator password for the LMEE PostgreSQL flexible server"
  type        = string
  sensitive   = true
}

variable "lmee_db_name" {
  description = "Application database created on the LMEE PostgreSQL flexible server"
  type        = string
  default     = "lmee"
}

variable "keycloak_realm" {
  description = "Keycloak realm that issues the tokens the backend validates"
  type        = string
  default     = "lmee"
}

variable "keycloak_db_name" {
  description = "Database Keycloak owns on the LMEE PostgreSQL flexible server"
  type        = string
  default     = "keycloak"
}

variable "keycloak_admin_username" {
  description = "Bootstrap admin username for the Keycloak master realm"
  type        = string
}

variable "keycloak_admin_password" {
  description = "Bootstrap admin password for the Keycloak master realm"
  type        = string
  sensitive   = true
}
