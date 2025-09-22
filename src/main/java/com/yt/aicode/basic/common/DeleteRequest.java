package com.yt.aicode.basic.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 删除请求
 *
 * @author wys17
 */
@Data
public class DeleteRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;
}
