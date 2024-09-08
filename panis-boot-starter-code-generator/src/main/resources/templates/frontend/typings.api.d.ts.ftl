declare namespace Api {
    namespace ${module} {
        <#assign upperTableName = upperPropertyTableName>
        type ${upperTableName} = Common.CommonRecord<{
            <#list tableColumnList as column>
            /** ${column.columnComment!} */
            ${column.propertyName}: ${column.typeScriptType};
            </#list>
        }>;

        /** ${upperTableName} search params */
        <#assign searchColumns = tableColumnList?filter(column -> column.search == '1')>
        type ${upperTableName}SearchParams = CommonType.RecordNullable<Pick<Api.${module}.${upperTableName},
            <#list searchColumns as column>
                '${column.propertyName}'<#if !column?is_last> | </#if>
            </#list>
            > & Api.Common.CommonSearchParams
        >;

        /** ${upperTableName} edit model */
        <#assign editColumns = tableColumnList?filter(column -> column.edit == '1')>
        type ${upperTableName}Edit = Pick<Api.${module}.${upperTableName},
        <#if editColumns??>
            <#list editColumns as column>
                '${column.propertyName}'<#if !column?is_last> | </#if>
            </#list>
            <#list tableColumnList as column>
                '${column.propertyName}'<#if !column?is_last> | </#if>
            </#list>
            <#else>
            >;
        </#if>


        /** ${upperTableName} list */
        type ${upperTableName}List = Common.PaginatingQueryRecord<${upperTableName}>;

    }
}
