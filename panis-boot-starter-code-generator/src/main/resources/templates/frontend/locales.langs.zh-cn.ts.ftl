<#assign i18nColumns = tableColumnList?filter(column -> column.i18n == '1')>
const local: App.I18n.Schema = {
    page: {
        ${module?lower_case}: {
            ${propertyTableName}: {
                <#list i18nColumns as column>
                ${column.propertyName}: '${column.columnComment}',
                </#list>
                form: {
                <#list i18nColumns as column>
                    <#if column.renderType! == '' || column.renderType! == 'input'>
                    ${column.propertyName}: '请输入${column.columnComment}'<#if !column?is_last>,</#if>
                    <#else>
                    ${column.propertyName}: '请选择${column.columnComment}'<#if !column?is_last>,</#if>
                    </#if>
                </#list>
                }
            }
        },
    }
}