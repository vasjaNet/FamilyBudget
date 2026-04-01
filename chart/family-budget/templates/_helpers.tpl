{{/*
Common labels
*/}}
{{- define "family-budget.labels" -}}
helm.sh/chart: {{ include "family-budget.chart" . }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Chart name and version
*/}}
{{- define "family-budget.chart" -}}
{{ .Chart.Name }}-{{ .Chart.Version | replace "+" "_" }}
{{- end }}

{{/*
Service image (with global registry + tag support)
*/}}
{{- define "family-budget.image" -}}
{{- if .Values.global.image.registry -}}
{{ .Values.global.image.registry }}/{{ .imageName }}:{{ .Values.global.image.tag | default "latest" }}
{{- else -}}
{{ .imageName }}:{{ .Values.global.image.tag | default "latest" }}
{{- end -}}
{{- end }}

{{/*
Labels with app selector
*/}}
{{- define "family-budget.matchLabels" -}}
app.kubernetes.io/name: {{ .name }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Kebab-case name for a service (e.g. userService -> user-service)
*/}}
{{- define "family-budget.serviceName" -}}
{{ .name | kebabcase }}
{{- end }}
