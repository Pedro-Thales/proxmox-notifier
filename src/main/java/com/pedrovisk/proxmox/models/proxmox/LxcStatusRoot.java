package com.pedrovisk.proxmox.models.proxmox;

import lombok.*;
import lombok.Data;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class LxcStatusRoot {
    List<ContainerStatus> data;
}
