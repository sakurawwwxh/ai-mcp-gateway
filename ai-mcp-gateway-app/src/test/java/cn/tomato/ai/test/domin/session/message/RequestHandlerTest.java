package cn.tomato.ai.test.domin.session.message;

import cn.tomato.ai.domain.session.model.valobj.McpSchemaVO;
import cn.tomato.ai.domain.session.service.message.handle.IRequestHandler;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Wxh
 * @date 2026年06月02日 22:00
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RequestHandlerTest {

    @Resource
    private IRequestHandler  toolsListHandler;

    @Test
    public void test_handle() {
        McpSchemaVO.JSONRPCResponse handle = toolsListHandler.
                handle("gateway_001",
                        new McpSchemaVO.JSONRPCRequest("2.0","tool/list","a355a5f7-0",""));
        log.info("测试结果:{}", JSON.toJSONString(handle.result()));
    }
}
