<script setup lang="ts">
interface ButtonProps {
  id: string;
  title: string;
  toggle?: boolean;
  onClick: () => void;
}

const props = defineProps<ButtonProps>();
</script>

<template>
  <button
    :id="props.id"
    :title="title"
    @click="onClick"
    :class="{ on: toggle }"
  >
    <slot></slot>
  </button>
</template>

<style scoped>
button {
  background-color: DarkSeaGreen;
  border: none;
  border-radius: 10px;
  color: white;
  padding: 12px 16px;
  font-size: 16px;
  cursor: pointer;
  width: 50px;
  height: 50px;
}

button:hover {
  background-color: SeaGreen;
}

button.on {
  background-color: SeaGreen;
}

button.on:hover {
  background-color: DarkSeaGreen;
}
</style>