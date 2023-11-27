resource "aws_cloudwatch_dashboard" "main" {
  dashboard_name = var.service_name
  dashboard_body = <<DASHBOARD
{
    "widgets": [
        {
            "type": "metric",
            "properties": {
                "view": "timeSeries",
                "stacked": true,
                "metrics": [
                    [ "${var.cloudwatch_namespace}", "detected violations.count", "scan type", "exhaustion" ],
                    [ ".", "image scans.count", ".", "." ]
                ],
                "region": "${var.aws_region}",
                "title": "Exhaustion comparision"
            }
        },
        {
            "type": "metric",
            "properties": {
                "metrics": [
                    [ { "expression": "SELECT SUM(\"detected people.count\") FROM SCHEMA(\"${var.cloudwatch_namespace}\", \"scan type\")", "label": "Total", "id": "q1"} ],
                    [ "${var.cloudwatch_namespace}", "detected people.count", "scan type", "ppe", { "id": "m1", "region": \"${var.aws_region}\" } ],
                    [ "${var.cloudwatch_namespace}", "detected people.count", "scan type", "exhaustion", { "id": "m2", "region": "${var.aws_region}" } ]
                ],
                "view": "timeSeries",
                "stacked": false,
                "region": "${var.aws_region}",
                "stat": "Sum",
                "period": 5,
                "title": "detected people"
            }
        },
        {
            "type": "metric",
            "x": 12,
            "y": 0,
            "width": 4,
            "height": 6,
            "properties": {
                "metrics": [
                    [ { "expression": "SELECT SUM(\"image scans.count\") FROM SCHEMA(\"${var.cloudwatch_namespace}\", \"scan type\")", "label": "Last 30 days", "id": "q1", "stat": "Sum", "period": 2592000, "region": "${var.aws_region}" } ]
                ],
                "sparkline": false,
                "view": "singleValue",
                "region": "${var.aws_region}",
                "stat": "Sum",
                "period": 2592000,
                "stacked": false,
                "title": "Scanned images",
                "singleValueFullPrecision": false,
                "setPeriodToTimeRange": true,
                "trend": false,
                "liveData": false
            }
        },
        {
            "type": "metric",
            "x": 16,
            "y": 0,
            "width": 6,
            "height": 6,
            "properties": {
                "metrics": [
                    [ "${var.cloudwatch_namespace}", "detected violations.count", "scan type", "ppe" ],
                    [ "...", "exhaustion" ]
                ],
                "view": "timeSeries",
                "stacked": false,
                "region": "${var.aws_region}",
                "stat": "Sum",
                "period": 900,
                "title": "Detected violations"
            }
        }
    ]
}
  DASHBOARD
}

