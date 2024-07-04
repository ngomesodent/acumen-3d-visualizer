<script setup lang="ts">
import MapComponent from "@/components/model/MapComponent.vue";
import ActionsComponent from "@/components/model/ActionsComponent.vue";
import { ref } from "vue";

const isPlayingSimulation = ref<boolean>(false);
const areShownBuildings = ref<boolean>(true);
const simulationSpeed = ref<number>(1);

const playPauseSimulation = (value: boolean): void => {
  isPlayingSimulation.value = value;
};

const showHideBuildings = (value: boolean): void => {
  areShownBuildings.value = value;
};

const changeSimulationSpeed = (value: number): void => {
  simulationSpeed.value = value;
};
</script>

<template>
  <div id="model">
    <ActionsComponent
      @play-pause-simulation="playPauseSimulation"
      @show-hide-buildings="showHideBuildings"
      @change-simulation-speed="changeSimulationSpeed"
    />
    <Suspense>
      <MapComponent
        :is-playing-simulation="isPlayingSimulation"
        :are-shown-buildings="areShownBuildings"
        :simulation-speed="simulationSpeed"
      />
    </Suspense>
  </div>
</template>

<style scoped>
#model {
  display: flex;
  flex-direction: row;
  gap: 20px;
  margin-top: 20px;
}
</style>