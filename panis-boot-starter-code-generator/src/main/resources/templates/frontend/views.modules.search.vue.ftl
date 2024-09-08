<script setup lang="ts">
import { $t } from '@/locales';
import { useDict } from '@/hooks/business/dict';

defineOptions({
    name: '${entity}Search'
});

interface Emits {
    (e: 'reset'): void;
    (e: 'search'): void;
}

const emit = defineEmits<Emits>();

const model = defineModel<Api.${module}.${upperPropertyTableName}SearchParams>('model', { required: true });

const { dictOptions } = useDict();

function reset() {
    emit('reset');
}

function search() {
    emit('search');
}
</script>

<#assign searchColumns = tableColumnList?filter(column -> column.search == '1')>
<template>
<NCard :bordered="false" size="small" class="card-wrapper">
    <NForm :model="model" label-placement="left" :show-feedback="false" :label-width="80">
        <NGrid responsive="screen" item-responsive :x-gap="8" :y-gap="8" cols="1 s:1 m:5 l:5 xl:5 2xl:5">
            <NGridItem span="4">
                <NGrid responsive="screen" item-responsive :x-gap="8">
                    <#list searchColumns as column>
                    <#assign renderType = column.renderType>
                    <NFormItemGi span="24 s:8 m:6" :label="$t('page.${module?lower_case}.${propertyTableName}.${column.propertyName}')" path="${column.propertyName}">
                        <#if renderType == 'select'>
                        <NSelect
                            v-model:value="model.${column.propertyName}"
                            size="small"
                            :placeholder="$t('page.${module?lower_case}.${propertyTableName}.form.${column.propertyName}')"
                            :options="dictOptions('${column.dictCode}')"
                            clearable
                        />
                        <#elseif renderType == 'radio'>
                        <NRadioGroup v-model:value="model.${column.propertyName}">
                            <NRadio v-for="item in dictOptions('${column.dictCode}')" :key="item.value" :value="item.value" :label="item.label" />
                        </NRadioGroup>
                        <#else>
                        <NInput v-model:value="model.${column.propertyName}" size="small" :placeholder="$t('page.${module?lower_case}.${propertyTableName}.form.${column.propertyName}')" />
                        </#if>
                    </NFormItemGi>
                    </#list>
                </NGrid>
            </NGridItem>
            <NGridItem>
                <NFormItemGi span="24 s:8 m:6">
                    <NSpace class="w-full" justify="end">
                        <NButton type="primary" ghost @click="search">
                            <template #icon>
                                <icon-ic-round-search class="text-icon" />
                            </template>
                            {{ $t('common.search') }}
                        </NButton>
                        <NButton quaternary @click="reset">
                            <template #icon>
                                <icon-ic-round-refresh class="text-icon" />
                            </template>
                            {{ $t('common.reset') }}
                        </NButton>
                    </NSpace>
                </NFormItemGi>
            </NGridItem>
        </NGrid>
    </NForm>
</NCard>
</template>

<style scoped></style>
