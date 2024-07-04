<script setup lang="ts">
interface IconProps {
  id: string;
  name: string;
}

const props = defineProps<IconProps>();
</script>

<template>
  <!-- This project uses Font Awesome -->
  <i
    :id="props.id"
    :class="`fa-solid fa-${props.name}`"
  ></i>
</template>