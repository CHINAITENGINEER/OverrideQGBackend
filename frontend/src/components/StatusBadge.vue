<script setup>
import { computed } from 'vue';

const props = defineProps({
  code: { type: Number, required: true },
});

const map = {
  0: ['待处理', 'badge--0'],
  1: ['处理中', 'badge--1'],
  2: ['已完成', 'badge--2'],
  3: ['已取消', 'badge--3'],
};

const label = computed(() => map[props.code]?.[0] ?? '未知');
const cls = computed(() => map[props.code]?.[1] ?? '');
</script>

<template>
  <span class="badge" :class="cls">{{ label }}</span>
</template>
