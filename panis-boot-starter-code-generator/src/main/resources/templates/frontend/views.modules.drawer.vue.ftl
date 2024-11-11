<script setup lang="ts">
import { computed, reactive, watch } from 'vue';
import { useFormRules, useNaiveForm } from '@/hooks/common/form';
import { $t } from '@/locales';
import { fetchAdd${upperPropertyTableName}, fetchUpdate${upperPropertyTableName}Info } from '@/service/api';
import { useDict } from '@/hooks/business/dict';

defineOptions({
    name: '${entity}OperateDrawer'
});

interface Props {
    /** the type of operation */
    operateType: NaiveUI.TableOperateType;
    /** the edit row data */
    rowData?: Api.${module}.${upperPropertyTableName} | null;
}

<#assign requiredColumns = tableColumnList?filter(column -> column.required == '1')>
const props = defineProps<Props>();

interface Emits {
    (e: 'submitted'): void;
}

const emit = defineEmits<Emits>();

const visible = defineModel<boolean>('visible', {
    default: false
});

const { dictOptions } = useDict();
const { formRef, validate, restoreValidation } = useNaiveForm();
<#if requiredColumns?has_content>
const { defaultRequiredRule } = useFormRules();
</#if>

const title = computed(() => {
    const titles: Record<NaiveUI.TableOperateType, string> = {
        add: $t('common.add'),
        edit: $t('common.edit'),
    };
    return titles[props.operateType];
});

type Model = Api.${module}.${upperPropertyTableName};

const model: Model = reactive(createDefaultModel());

function createDefaultModel(): Model {
    return {
        <#list tableColumnList as column>
        ${column.propertyName}: <#if column.propertyName == 'status'>'1'<#elseif column.typeScriptType== 'number'>0<#else>''</#if><#if !column?is_last>,</#if>
        </#list>
    };
}
<#if requiredColumns?has_content>

type RuleKey = Extract<keyof Model,
    <#list requiredColumns as column>
'${column.propertyName}'<#if !column?is_last> | </#if>
</#list>
>;

const rules: Record<RuleKey, App.Global.FormRule> = {
    <#list requiredColumns as column>
    ${column.propertyName}: defaultRequiredRule<#if !column?is_last>,</#if>
    </#list>
};
</#if>

function handleInitModel() {
    Object.assign(model, createDefaultModel());

    if (!props.rowData) return;

    if (props.operateType === 'edit' && props.rowData) {
        Object.assign(model, props.rowData);
    }
}

function closeDrawer() {
    visible.value = false;
}

const isAdd = computed(() => props.operateType === 'add');

async function handleSubmit() {
    await validate();
    const func = isAdd.value ? fetchAdd${upperPropertyTableName} : fetchUpdate${upperPropertyTableName}Info;
    const { error, data } = await func(model);
    if (!error && data) {
        window.$message?.success(isAdd.value ? $t('common.addSuccess') : $t('common.updateSuccess'));
        closeDrawer();
        emit('submitted');
    }
}

watch(visible, () => {
    if (visible.value) {
        handleInitModel();
        restoreValidation();
    }
});
</script>

<template>
<#assign addedColumns = tableColumnList?filter(column -> column.added == '1')>
<NDrawer v-model:show="visible" display-directive="show" :width="360">
    <NDrawerContent :title="title" :native-scrollbar="false" closable>
        <NForm ref="formRef" :model="model" <#if requiredColumns?has_content>:rules="rules"</#if>>
            <#list addedColumns as column>
            <#assign renderType = column.renderType>
            <NFormItem :label="$t('page.${module?lower_case}.${propertyTableName}.${column.propertyName}')" path="${column.propertyName}">
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
                <NInput v-model:value="model.${column.propertyName}" :placeholder="$t('page.${module?lower_case}.${propertyTableName}.form.${column.propertyName}')" />
                </#if>
            </NFormItem>
            </#list>
        </NForm>
        <template #footer>
            <NSpace>
                <NButton quaternary @click="closeDrawer">{{ $t('common.cancel') }}</NButton>
                <NButton type="primary" @click="handleSubmit">{{ $t('common.confirm') }}</NButton>
            </NSpace>
        </template>
    </NDrawerContent>
</NDrawer>
</template>

<style scoped></style>
