package com.pedrovisk.proxmox.models.proxmox;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class VmProxmoxSupport {
    @JsonProperty("pbs-dirty-bitmap-savevm")
    public boolean pbsDirtyBitmapSavevm;
    @JsonProperty("pbs-dirty-bitmap-migration")
    public boolean pbsDirtyBitmapMigration;
    @JsonProperty("query-bitmap-info")
    public boolean queryBitmapInfo;
    @JsonProperty("backup-max-workers")
    public boolean backupMaxWorkers;
    @JsonProperty("pbs-dirty-bitmap")
    public boolean pbsDirtyBitmap;
    @JsonProperty("pbs-masterkey")
    public boolean pbsMasterkey;
    @JsonProperty("pbs-library-version")
    public String pbsLibraryVersion;
}
