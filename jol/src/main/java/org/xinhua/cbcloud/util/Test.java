package org.xinhua.cbcloud.util;

import com.sun.deploy.util.StringUtils;

import java.util.Arrays;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        StringBuffer entityIds = new StringBuffer();
        entityIds.append("200012017050876139334");
        entityIds.append(",");
        entityIds.append("200012017050876139362");
        entityIds.append(",");
        entityIds.append("200012017050700106191");
        entityIds.append(";");

        String str = entityIds.toString();
        List<Object> list = Arrays.asList(str);
        System.out.println(StringUtils.join(list, ","));
    }
}
