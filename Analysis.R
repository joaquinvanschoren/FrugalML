source("HugeMatrixFunctions.R")
source("SciCl.R") 
source("GetCleanMeasuresFromSource.R") 
source("DataSetsQualities.R") 

# state for reproducibility of results 
srv() 

# create a matrix from a function or from a script
matrixGenericType <- TRUE 
if (matrixGenericType) {
    evaluations <- getCleanMeasures() 

    pAUC <- evaluations[[1]] 
    pTime <- evaluations[[2]] 
    
    pAUC <- imputeEmptyVals(pAUC) 
    pTime <- imputeEmptyVals(pTime) 

    w <- 0.1 
    
    # compute matrix with original method or from separately imputed values   
    originalMethod <- FALSE 
    if (originalMethod) {
        hugeMatrix <- createHugeMatrix(originData = evaluations, splitFactor = "task_id",
                                       p.w = w, p.normalize = TRUE, p.matrixEmptyValuesThrow = TRUE) 
        hugeMatrix <- imputeEmptyVals(hugeMatrix)         
    } else {
        normalize = TRUE 
        if (normalize) {
            sMatrix <- pAUC - w * log10(pTime + 1)  
            minValue <- apply(sMatrix, 1, min) 
            maxValue <- apply(sMatrix, 1, max)   
            for (i in 1:nrow(sMatrix)) {
                sMatrix[i, ] <- (sMatrix[i, ] - minValue[i]) / (maxValue[i] - minValue[i]) 
            }
            hugeMatrix <- sMatrix 
        } else {
            hugeMatrix <- pAUC - w * log10(pTime + 1)  
        }
    } 
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
decomposedMatrix <- makeSVDanalysis(resMatrix = hugeMatrix, p.numLatent = 15) 
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
   