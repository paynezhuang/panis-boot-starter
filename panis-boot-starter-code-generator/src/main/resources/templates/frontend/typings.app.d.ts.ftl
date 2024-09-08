<#assign i18nColumns = tableColumnList?filter(column -> column.i18n == '1')>
declare namespace App {
    namespace I18n {
        type Schema = {
            page: {
                ${module?lower_case}: {
                   ${propertyTableName}: {
                        <#list i18nColumns as column>
                        ${column.propertyName}: string;
                        </#list>
                        form: {
                            <#list i18nColumns as column>
                            ${column.propertyName}: string;
                            </#list>
                        };
                    };
                };
            }
        }
    }
}