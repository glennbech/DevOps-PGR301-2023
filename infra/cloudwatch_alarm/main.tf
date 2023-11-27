resource "aws_cloudwatch_metric_alarm" "threshold" {
  alarm_name = "${var.alarm_prefix}-${var.metric_name}"
  namespace = var.alarm_prefix
  metric_name = var.metric_name

  comparison_operator = var.comparison_operator
  threshold = var.threshold
  evaluation_periods = "2"
  period = "60"
  statistic = var.statistic

  alarm_description = "${var.metric_name} reached ${var.comparison_operator} ${var.statistic} ${var.threshold} \n${var.alarm_context}"
  alarm_actions = [aws_sns_topic.user_updates.arn]
}

resource "aws_sns_topic" "user_updates" {
  name = "${var.alarm_prefix}-alarm-topic"
}
resource "aws_sns_topic_subscription" "user_updates_sqs_target" {
  topic_arn = aws_sns_topic.user_updates.arn
  protocol  = "email"
  endpoint  = var.alarm_email
}
