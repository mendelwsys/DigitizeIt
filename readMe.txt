Raster vectorizer project.

The program is intended for automatic vectorization of raster images and their subsequent conversion to shp format.

Developed on the basis of the core of the project of its own GIS system (ToyGis).
At startup, the application is built from functional programming blocks based on the xml configuration.
Has two language interfaces, English (default) and Russian.

Documentation is in the DOC(S) folder (in Russian)
Raster samples for digitization are located in the MAPDIR folder.

The vectorization process consists of the following steps

1. Select and load a raster image.
2. Select the color on the raster whose objects will be digitized.
3. Set up digitization parameters, incl. filtering parameters for digitizing (the median filter for the raster is applied).
4. Create a vector layer, it is created automatically above the raster layer after pressing the vectorize button.
5. Save the vector layer to the list of layers for transfer to a shp file (on the left side of the screen), it also allows you to select the drawing styles of the layer.
6. Select the translation options in the shp file.
7. Translate to *shp - the result is created in the MAPDIR folder, during the translation, you can geo-reference
raster using the projection description file and geo-coordinates of the raster *TAB.

