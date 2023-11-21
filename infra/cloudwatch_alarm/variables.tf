variable "alarm_prefix" {
  type = string
  description = "Unique name for alarm"
}

variable "threshold" {
  type = number
}

variable "alarm_email" {
  type = string
  description = "The email to send to"
}

variable "metric_name" {
  type = string
  description = "Name for the metric to monitor"
}

variable "comparison_operator" {
  type = string
  default = "GreaterThanThreshold"
}

variable "statistic" {
  type = string
  default = "Maximum"
}

variable "alarm_context" {
  type = string
  description = "Add context to the email when the alarm is reached"
  default = ""
}