<script setup lang="tsx">
import { NButton, NPopconfirm } from 'naive-ui';
import type { Ref } from 'vue';
import { ref } from 'vue';
import { useAppStore } from '@/store/modules/app';
import { useAuth } from '@/hooks/business/auth';
import { useTable, useTableOperate } from '@/hooks/common/table';
import { $t } from '@/locales';
import { transDeleteParams } from '@/utils/common';
import { fetchDelete${upperPropertyTableName}, fetchGet${upperPropertyTableName}List } from '@/service/api';
import { useDict } from '@/hooks/business/dict';
import ${upperPropertyTableName}Search from './modules/${dashTableName}-search.vue';
import ${upperPropertyTableName}OperateDrawer from './modules/${dashTableName}-operate-drawer.vue';

defineOptions({
    name: '${entity}Page'
});

const operateType = ref<NaiveUI.TableOperateType>('add');

const appStore = useAppStore();

const { hasAuth } = useAuth();

const { dictTag } = useDict();

const editingData: Ref<Api.${module}.${upperPropertyTableName} | null> = ref(null);

<#assign listColumns = tableColumnList?filter(column -> column.list == '1')>
<#assign searchColumns = tableColumnList?filter(column -> column.search == '1')>
const { columns, columnChecks, data, loading, getData, getDataByPage, mobilePagination, searchParams, resetSearchParams } = useTable({
    apiFn: fetchGet${upperPropertyTableName}List,
    apiParams: {
        page: 1,
        pageSize: 20,
        <#list searchColumns as column>
        ${column.propertyName}: null<#if !column?is_last>,</#if>
        </#list>
    },
    columns: () => [
        {
            key: 'index',
            title: $t('common.index'),
            width: 64,
            align: 'center'
        },
        <#list listColumns as column>
        {
            key: '${column.propertyName}',
            title: $t('page.${module?lower_case}.${propertyTableName}.${column.propertyName}'),
            align: 'center',
            minWidth: 100
        },
        </#list>
        {
            key: 'operate',
            title: $t('common.operate'),
            align: 'center',
            width: 200,
            minWidth: 200,
            render: row => (
                <div class="flex-center gap-8px">
                    {hasAuth('${permission}:update') && (
                        <NButton type="primary" quaternary size="small" onClick={() => edit(row)}>
                            {$t('common.edit')}
                        </NButton>
                    )}
                    {hasAuth('${permission}:delete') && (
                        <NPopconfirm onPositiveClick={() => handleDelete(row.id)}>
                            {{
                                default: () => $t('common.confirmDelete'),
                                trigger: () => (
                                    <NButton type="error" quaternary size="small">
                                        {$t('common.delete')}
                                    </NButton>
                                )
                            }}
                        </NPopconfirm>
                    )}
                </div>
            )
        }
    ]
});

const { drawerVisible, openDrawer, checkedRowKeys, onDeleted, onBatchDeleted } = useTableOperate(data, getData);

function handleAdd() {

}

function edit(item: Api.${module}.${upperPropertyTableName}) {
    operateType.value = 'edit';
    editingData.value = { ...item };
    openDrawer();
}

async function handleDelete(id: string) {
    // request
    const { error, data: result } = await fetchDelete${upperPropertyTableName}(transDeleteParams([id]));
    if (!error && result) {
        await onDeleted();
    }
}

async function handleBatchDelete() {
    // request
    const { error, data: result } = await fetchDelete${upperPropertyTableName}(transDeleteParams(checkedRowKeys.value));
    if (!error && result) {
        await onBatchDeleted();
    }
}

</script>

<template>
<div class="min-h-500px flex-col-stretch gap-8px overflow-hidden lt-sm:overflow-auto">
    <${upperPropertyTableName}Search v-model:model="searchParams" @reset="resetSearchParams" @search="getDataByPage" />
    <NCard :bordered="false" class="sm:flex-1-hidden card-wrapper" content-class="flex-col">
        <TableHeaderOperation
            v-model:columns="columnChecks"
            :checked-row-keys="checkedRowKeys"
            :loading="loading"
            add-auth="${permission}:add"
            delete-auth="${permission}:delete"
            @add="handleAdd"
            @delete="handleBatchDelete"
            @refresh="getData"
        />
        <NDataTable
            v-model:checked-row-keys="checkedRowKeys"
            remote
            striped
            size="small"
            class="sm:h-full"
            :data="data"
            :scroll-x="962"
            :columns="columns"
            :flex-height="!appStore.isMobile"
            :loading="loading"
            :single-line="false"
            :row-key="row => row.id"
            :pagination="mobilePagination"
        />
        <${upperPropertyTableName}OperateDrawer v-model:visible="drawerVisible" :operate-type="operateType" :row-data="editingData" @submitted="getDataByPage" />
    </NCard>
</div>
</template>
