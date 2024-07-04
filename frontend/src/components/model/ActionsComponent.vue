<script setup lang="ts">
import { ref } from "vue";
import IconComponent from "@/components/ui/IconComponent.vue";
import ButtonComponent from "@/components/ui/ButtonComponent.vue";

const isPlayingSimulation = ref<boolean>(false);
const areShownBuildings = ref<boolean>(true);
const simulationSpeed = ref<number>(1);

const emit = defineEmits<{
  playPauseSimulation: [isPlayingSimulation: boolean];
  showHideBuildings: [areShownBuildings: boolean];
  changeSimulationSpeed: [simulationSpeed: number];
}>();

const playPauseSimulation = (): void => {
  isPlayingSimulation.value = !isPlayingSimulation.value;
  emit("playPauseSimulation", isPlayingSimulation.value);
};

const showHideBuildings = (): void => {
  areShownBuildings.value = !areShownBuildings.value;
  emit("showHideBuildings", areShownBuildings.value);
};

const slowDownSimulation = (): void => {
  simulationSpeed.value -= 1;
  emit("changeSimulationSpeed", simulationSpeed.value);
};

const speedUpSimulation = (): void => {
  simulationSpeed.value += 1;
  emit("changeSimulationSpeed", simulationSpeed.value);
};
</script>

<template>
  <div id="actions">
    <ButtonComponent
      id="play-pause-button"
      :title="isPlayingSimulation ? 'Pause simulation' : 'Play simulation'"
      :toggle="isPlayingSimulation"
      :on-click="playPauseSimulation"
    >
      <IconComponent
        id="play-pause-icon"
        :name="isPlayingSimulation ? 'pause' : 'play'"
      ></IconComponent>
    </ButtonComponent>
    <div id="speed-controls">
      <ButtonComponent
        id="backward-button"
        title="Slow down simulation"
        :on-click="slowDownSimulation"
      >
        <IconComponent
          id="backward-icon"
          name="backward"
        ></IconComponent>
      </ButtonComponent>
      <p>x{{ simulationSpeed }}</p>
      <ButtonComponent
        id="forward-button"
        title="Speed up simulation"
        :on-click="speedUpSimulation"
      >
        <IconComponent
          id="forward-icon"
          name="forward"
        ></IconComponent>
      </ButtonComponent>
    </div>
    <ButtonComponent
      id="building-button"
      :title="areShownBuildings ? 'Hide buildings' : 'Show buildings'"
      :toggle="areShownBuildings"
      :on-click="showHideBuildings"
    >
      <IconComponent
        id="building-icon"
        name="building"
      ></IconComponent>
    </ButtonComponent>
  </div>
</template>

<style scoped>
#actions {
  display: flex;
  flex-direction: column;
  margin-left: 10px;
  gap: 10px;
}

#speed-controls {
  background-color: DarkSeaGreen;
  border-radius: 10px;
}

p {
  font-family: Inter, sans-serif;
  color: white;
  text-align: center;
}
</style>