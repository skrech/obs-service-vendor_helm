# OBS Source Service `obs-service-vendor-helm`

## Overview

A source service for [Open Build Service](https://openbuildservice.org/) (OBS) that vendors Helm charts. This tool simplifies the process of packaging Helm charts by:

- Downloading third-party dependency sub-charts into the appropriate directory inside your main Helm chart;
- Archives the main chart's contents according to a glob pattern. The archive is ready for consumption from OBS' build recipe for Helm charts.

This service is expected to be used in combination with other OBS source services like [tar_scm](https://github.com/openSUSE/obs-service-tar_scm), [recompress](https://github.com/openSUSE/obs-service-recompress) and others.

## Installation

This service is available from the standard repository for OBS source services: [openSUSE:Tools](https://build.opensuse.org/project/show/openSUSE:Tools). It's also available in Tumbleweed.

To install the service:

```bash
sudo zypper in obs-service-vendor_helm
```

[!WARNING]: This project is not yet available in the mentioned repositories since it's in the process of submission.

## Usage

Example usage in your project's `_service` file when the repository is named `your-repo` and the chart that you are interested in is llocated at a subdirectory `charts/your-chart` (where the `Chart.yaml` file is):

```xml
<services>
  <service name="tar_scm" mode="manual">
    <param name="url">https://your-domain.com/your-repo.git</param>
    <param name="revision">@PARENT_TAG@</param>
    <param name="version">_none_</param>
    <param name="subdir">charts/your-chart</param>
    <param name="extract">values.yaml</param>
    <param name="extract">Chart.yaml</param>
  </service>
  <service name="vendor_helm" mode="manual">
    <param name="subdir">your-repo/charts/your-chart</param>
    <param name="include">{charts,templates}</param>
  </service>
  <service name="recompress" mode="manual">
    <param name="file">*.tar</param>
    <param name="compression">gz</param>
  </service>
</services>
```

This would produce a file named `contents.tar.gz` that contains the `charts` (with all third-party sub-charts downloaded) and `templates` directories.

## Options

For the available options check the [.service](vendor_helm.service) file.

## License

Copyright (c) 2026 Kristiyan Kanchev <kristiyan.kanchev@suse.com>

This project is licensed under the GNU General Public License v2.0 - see the [LICENSE](LICENSE) file for details.
