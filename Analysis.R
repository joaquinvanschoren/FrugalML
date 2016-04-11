source("HugeMatrixFunctions.R")
source("SciCl.R") 

# state for reproducibility of results 
set.seed(7L) 

# create a matrix from a function or from a script
matrixGenericType <- TRUE 
if (matrixGenericType) {
    evaluations <- loadEvaluations()
    hugeMatrix <- createHugeMatrix(originData = evaluations, splitFactor = "task_id",
                                   p.w = 0.5, p.normalize = FALSE, p.matrixEmptyValuesThrow = TRUE)
    hugeMatrix <- imputeEmptyVals(hugeMatrix)
} else {
    source("HugeMatrix.R")
} 

# create files with a Pareto front for every data set and every cluster
makeParetoFront <- FALSE
if (makeParetoFront) {
    source("Clustering.R")
}

# image with Pareto front for every data set
makeIndividualParetoFrontForEveryDataSet <- FALSE 
if (makeIndividualParetoFrontForEveryDataSet) {
    source("DataPresent.R") 
}

# perform SVD analysis and decompose a matrix
decomposedMatrix <- makeSVDanalysis(resMatrix = hugeMatrix, p.numLatent = 20) 
resMatrixDecomposed_d <- decomposedMatrix$u
resMatrixDecomposed_a <- t(decomposedMatrix$v) 
featureWeights <- decomposedMatrix$d
findSSE(
    p.matrix = resMatrixDecomposed_d, v.features = featureWeights, p.useWeights = FALSE)

# use cluster analysis
numDataSets <- drawSilhouette(p.matrix = hugeMatrix, p.k = 56L) 

# get clusters for data sets with kMeans algorithm
clusters <- getkMeansClusters(p.matrix = resMatrixDecomposed_d, p.numClusters = numDataSets) 
table(clusters) 
 
# identify a limited number of data sets for detailed analysis
selectedDataSets <- rownames(getMedoids(p.matrix = hugeMatrix, p.numMedoids = numDataSets)) 

# create heatmaps and place them in files 
dendrograms <- createHeatMapsComplex(
    p.matrix = hugeMatrix, p.dataSetsDecomposed = resMatrixDecomposed_d,
    p.algorithmsDecomposed = resMatrixDecomposed_a, p.distfunction = 'euclidean',
    p.savePath = 'plots/', p.numOfIntervals = 7) 

# get clusters for data sets with hierarchical clustering
hclusters <- getHierarchicalClusters(p.dendrogram = dendrograms$rows, p.numClusters = numDataSets) 
table(hclusters) 

# perform PCA analysis and put plots in files
pcaPlots(p.matrix = resMatrixDecomposed_d, p.clusters = clusters, p.alternative = FALSE) 

# stude visualization received with t-SNE method and resistance against noise
tsnePlot(p.matrix = resMatrixDecomposed_d, p.names = rownames(hugeMatrix), noiseLevel = 0, 
         p.clusters = clusters) 

# create a plot with index w and Frugality score values
plotAlgorithmsIndex <- FALSE 
 if (plotAlgorithmsIndex) {
    source("LinearPlots.R")
 } 
   