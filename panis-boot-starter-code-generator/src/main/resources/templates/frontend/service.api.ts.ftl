import { request } from '@/service/request';

<#assign upperTableName = upperPropertyTableName>

// =============== ${upperTableName} Begin ===============

/** get ${propertyTableName} list */
export function fetchGet${upperTableName}List(params?: Api.${module}.${upperTableName}SearchParams) {
    return request<Api.${module}.${upperTableName}List>({
        url: '/${table.name}/page',
        method: 'GET',
        params
    });
}

/** add ${propertyTableName} info */
export function fetchAdd${upperTableName}(data: Api.${module}.${upperTableName}Edit) {
    return request<boolean>({
        url: '/${table.name}/',
        method: 'POST',
        data
    });
}

/** update ${propertyTableName} info */
export function fetchUpdate${upperTableName}Info(data: Api.${module}.${upperTableName}Edit) {
    return request<boolean>({
        url: '/${table.name}/',
        method: 'PUT',
        data
    });
}

/** edit delete ${propertyTableName} */
export function fetchDelete${upperTableName}(data: Api.Common.DeleteParams) {
    return request<boolean>({
        url: '/${table.name}/',
        method: 'DELETE',
        data
    });
}

// =============== ${upperTableName} End  ===============