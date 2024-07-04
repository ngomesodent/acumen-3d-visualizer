<script setup lang="ts">
import { onMounted, watch } from "vue";
import "cesium/Build/Cesium/Widgets/widgets.css";
import * as Cesium from "cesium";
import { addCZMLDataSource, setEntity, setup } from "@/utils/cesium";
import { filenames, paths } from "@/utils/project-paths";

interface MapProps {
  isPlayingSimulation: boolean;
  areShownBuildings: boolean;
  simulationSpeed: number;
}

const props = withDefaults(defineProps<MapProps>(), {
  isPlayingSimulation: false,
  areShownBuildings: true,
  simulationSpeed: 1,
});

onMounted(async () => {
  watch(
    () => props.isPlayingSimulation,
    (newValue: boolean) => {
      viewer.clock.shouldAnimate = newValue;
    }
  );

  watch(
    () => props.areShownBuildings,
    (newValue: boolean) => {
      if (!tileset) return console.log("Tileset not found");
      tileset.show = newValue;
    }
  );

  watch(
    () => props.simulationSpeed,
    (newValue: number) => {
      viewer.clock.multiplier = newValue;
    }
  );

  const { viewer, tileset } = await setup(
    import.meta.env.VITE_CESIUM_ION_ACCESS_TOKEN,
    import.meta.env.VITE_NEW_YORK_ASSET_ID
  );

  if (!tileset) return console.log("Something went wrong when retrieving the tileset");

  const path: string = `${paths.scenarios}${filenames.scenario}.czml`;
  const czml: Cesium.DataSource = await addCZMLDataSource(viewer, path);

  czml.entities.values.forEach((entity) => {
    const cesiumMilkTruck: Cesium.Entity | null = setEntity(czml, entity.id);
    if (!cesiumMilkTruck) {
      return console.log(`Entity with id ${entity.id} not found`);
    }
  });

  viewer.scene.debugShowFramesPerSecond = true;
});
</script>

<template>
  <div id="cesiumContainer"></div>
</template>

<style scoped>
#cesiumContainer {
  width: 80%;
  height: 80%;
}
</style>