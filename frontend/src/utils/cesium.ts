import * as Cesium from "cesium";

//Extends the Window interface to add CESIUM_BASE_URL otherwise it doesn't recognize it
declare global {
  interface Window {
    CESIUM_BASE_URL: string;
  }
}
// The URL on your server where CesiumJS's static files are hosted.
window.CESIUM_BASE_URL = "/public";

// Set up Cesium and the viewer and add the tileset, with a given access token and tileset id
export const setup = async (
  accessToken: string,
  assetId: number
): Promise<{
  viewer: Cesium.Viewer;
  tileset: Cesium.Cesium3DTileset | null;
}> => {
  // Grant CesiumJS access to your ion assets
  Cesium.Ion.defaultAccessToken = accessToken;

  const viewer: Cesium.Viewer = new Cesium.Viewer("cesiumContainer", {
    terrainProvider: await Cesium.CesiumTerrainProvider.fromIonAssetId(1),
    animation: false,
    baseLayerPicker: false,
    fullscreenButton: false,
    geocoder: false,
    homeButton: false,
    sceneModePicker: false,
    timeline: false,
    navigationHelpButton: false,
    infoBox: false,
    selectionIndicator: false,
  });
  viewer.scene.globe.depthTestAgainstTerrain = true;

  let tileset: Cesium.Cesium3DTileset | null = null;
  try {
    tileset = await Cesium.Cesium3DTileset.fromIonAssetId(assetId, {
      enableCollision: true,
    });
    viewer.scene.primitives.add(tileset);
    await viewer.zoomTo(tileset);

    // Apply the default style if it exists
    const extras = tileset.asset.extras;
    if (
      Cesium.defined(extras) &&
      Cesium.defined(extras.ion) &&
      Cesium.defined(extras.ion.defaultStyle)
    ) {
      tileset.style = new Cesium.Cesium3DTileStyle(extras.ion.defaultStyle);
    }
  } catch (error) {
    console.log(error);
  }

  return { viewer, tileset };
};

export const addCZMLDataSource = async (
  viewer: Cesium.Viewer,
  path: string
): Promise<Cesium.DataSource> => {
  const czmlDataSource: Cesium.CzmlDataSource = await Cesium.CzmlDataSource.load(path);
  return await viewer.dataSources.add(czmlDataSource);
};

/* The function purpose is to set the entities, so they are as accurate as possible for the simulation,
 * but for the moment it only calculates the orientation of the model based on its velocity */
export const setEntity = (
  datasource: Cesium.DataSource,
  entityId: string
): Cesium.Entity | null => {
  const entity: Cesium.Entity | undefined = datasource.entities.getById(entityId);
  if (!entity) {
    return null;
  }
  entity.orientation = new Cesium.VelocityOrientationProperty(entity.position);
  return entity;
};