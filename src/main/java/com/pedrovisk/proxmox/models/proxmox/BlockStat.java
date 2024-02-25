package com.pedrovisk.proxmox.models.proxmox;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.Data;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlockStat {
    public long failed_zone_append_operations;
    public boolean account_invalid;
    public long failed_rd_operations;
    public long wr_total_time_ns;
    public long zone_append_total_time_ns;
    public long wr_highest_offset;
    public long rd_bytes;
    public long unmap_operations;
    public long failed_wr_operations;
    public long rd_operations;
    public long wr_merged;
    public long zone_append_merged;
    public long failed_unmap_operations;
    public long flush_total_time_ns;
    public long zone_append_bytes;
    public long invalid_flush_operations;
    public long wr_operations;
    public long unmap_total_time_ns;
    public long rd_merged;
    public ArrayList<Object> timed_stats;
    public long rd_total_time_ns;
    public long idle_time_ns;
    public long zone_append_operations;
    public long failed_flush_operations;
    public long wr_bytes;
    public boolean account_failed;
    public long flush_operations;
    public long invalid_zone_append_operations;
    public long unmap_merged;
    public long invalid_rd_operations;
    public long invalid_wr_operations;
    public long invalid_unmap_operations;
    public long unmap_bytes;
}
