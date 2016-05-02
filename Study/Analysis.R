source("HugeMatrixFunctions.R")
source("SciCl.R") 
source("GetCleanMeasuresFromSource.R") 
source("DataSetsQualities.R") 

# get the data from a local directory 
cleanedEvaluations <- getCleanMeasures(p.throwMissingValues = TRUE) 
hugeMatrix <- createHugeMatrixFromImputedMeasures(cleanedEvaluations, p.w = 0.1, p.normalize = FALSE, p.throwMissingValues = TRUE) 

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

# selected algorithms from Pareto front plots 
a1 <- grep("1079", colnames(hugeMatrix)) 
a2 <- grep("1191_LogitBoost.I0", colnames(hugeMatrix)) 
a3 <- grep("1191_LogitBoost.I20", colnames(hugeMatrix)) 
a4 <- grep("1191_LogitBoost.I10", colnames(hugeMatrix)) 
a5 <- grep("BayesNet", colnames(hugeMatrix)) 
a6 <- grep("1155", colnames(hugeMatrix)) 
a7 <- grep("1154", colnames(hugeMatrix)) 
a8 <- grep("1186", colnames(hugeMatrix)) 
a9 <- grep("1078", colnames(hugeMatrix)) 
a10 <- grep("1090", colnames(hugeMatrix)) 
a11 <- grep("HyperPipes", colnames(hugeMatrix)) 
a12 <- grep("1069", colnames(hugeMatrix)) 
a13 <- grep("RotationForest.I20", colnames(hugeMatrix))  
a14 <- grep("RotationForest.I40", colnames(hugeMatrix))  
a15 <- grep("RotationForest.I10", colnames(hugeMatrix))  
a16 <- grep("RandomSubSpace.I10", colnames(hugeMatrix)) 

selectedAlgorithms <- c(a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16) 

hugeMatrix <- hugeMatrix[, selectedAlgorithms] 

# perform SVD analysis and decompose a matrix
decomposedMatrix <- makeSVDanalysis(resMatrix = hugeMatrix, p.numLatent = 10) 
resMatrixDecomposed_d <- decomposedMatrix$u
resMatrixDecomposed_a <- t(decomposedMatrix$v) 
featureWeights <- decomposedMatrix$d 
findSSE(
    p.matrix = resMatrixDecomposed_d, v.features = featureWeights, p.useWeights = FALSE)

# use cluster analysis
numDataSets <- drawSilhouette(p.matrix = hugeMatrix, p.k = 30L) 

# get clusters for data sets with kMeans algorithm
clustersFromSource <- TRUE 
if (clustersFromSource) {
    clusters <- getOriginalClusters() 
} else {
    clusters <- getkMeansClusters(p.matrix = resMatrixDecomposed_d, p.numClusters = numDataSets)     
} 
table(clusters) 

# split the main matrix to clusters 
hugeMatrixFirst <- hugeMatrix[clusters == 1, ] 
hugeMatrixSecond <- hugeMatrix[clusters == 2, ] 

hugeMatrix <- hugeMatrixFirst 

hugeMatrix <- hugeMatrixSecond 
 
# identify a limited number of data sets for detailed analysis
selectedDataSets <- rownames(getMedoids(p.matrix = hugeMatrix, p.numMedoids = numDataSets)) 

# create heatmaps and place them in files 
dendrograms <- createHeatMapsComplex(
    p.matrix = hugeMatrix, p.dataSetsDecomposed = resMatrixDecomposed_d,
    p.algorithmsDecomposed = resMatrixDecomposed_a, p.distfunction = 'euclidean',
    p.savePath = 'plots/', p.numOfIntervals = 50) 
 
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
   