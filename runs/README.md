# Runs

Use this directory for execution logs:

- OpenSpec command usage
- implementation runs
- test runs
- build and release runs
- any stage action whose completion is required for promotion

Each run log should capture at minimum:

- stage
- owner / agent
- command or action summary
- started_at / completed_at
- outputs produced
- success / failure

Do not treat run logs as workflow state. `project.yaml` remains the only workflow authority.
