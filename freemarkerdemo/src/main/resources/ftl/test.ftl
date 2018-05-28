<html>
<head>
    <title>Freemarker模板小程序</title>

    <meta charset="UTF-8">
</head>
<body>
    <#assign linkm="周先生"/>

    ${names} 您好，欢迎使用FreeMarker静态技术${linkm}
    <#assign info={"id":"100","ename":"李先生","age":"33"}/>
    <br><br>
    id:${info.id}<br>
    名字:${info.ename}<br>
    年龄:${info.age}<br>
<#include "head.ftl">
<br>
<#if success=true>
    您已通过考验
<#else >
    您未通过考验
</#if>
<br>
<table>
    <#list list as goods>
    <tr>
        <td>${goods.id}</td>
        <td>${goods.goodsName}</td>
        <td>${goods.price}</td>
    </tr>
    </#list>
</table>
共 ${list?size}条记录
<br><br>
<#assign text="{'bank':'工商银行','account':'101150552055'}"/>
<#--json串转换为对象-->
<#assign data=text?eval/>
开户行:${data.bank}  账号:${data.account}
<br><br>
<#--日期格式化-->
当前日期:${today?date}<br>
当前时间:${today?time}<br>
当前日期+时间${today?datetime}<br>
日期格式化:${today?string("yyyy年MM月")}
<br><br><br><br>
数字格式化:${point?c}<#--默认没三个加个逗号-->
<br><br><br><br>
空值处理<br>
<#if aaa??>
    aaa变量存在
<#else >
    aaa 变量不存在
</#if>
<br><br><br><br>
缺失变量默认值<br>
${aaa!'默认值'}
</body>
</html>