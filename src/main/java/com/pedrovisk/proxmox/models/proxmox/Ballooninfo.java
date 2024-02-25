package com.pedrovisk.proxmox.models.proxmox;

import lombok.*;
import lombok.Data;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Ballooninfo {
    public int mem_swapped_in;
    public int major_page_faults;
    public long free_mem;
    public long actual;
    public long total_mem;
    public int minor_page_faults;
    public int last_update;
    public long max_mem;
    public int mem_swapped_out;
}
