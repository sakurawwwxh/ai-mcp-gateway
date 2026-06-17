package cn.tomato.ai.infrastructure.dao.po.base;

import lombok.Data;

/**
 * 分页查询基类
 * 提供分页参数及自动计算 limitStart/limitCount
 *
 * @author Wxh
 * @date 2026-06-15
 */
@Data
public class BasePagePO {
    /** 当前页码（从1开始） */
    private Integer page;
    /** 每页条数 */
    private Integer rows;
    /** SQL 偏移量 */
    private Integer limitStart;
    /** SQL 限制条数 */
    private Integer limitCount;

    public void setPage(Integer page) {
        this.page = page;
        calc();
    }

    public void setRows(Integer rows) {
        this.rows = rows;
        calc();
    }

    private void calc() {
        if (page != null && rows != null) {
            this.limitStart = (page - 1) * rows;
            this.limitCount = rows;
        }
    }
}