source("SciCl.R") 
source("HugeMatrixFunctions.R") 
source("GetCleanMeasuresFromSource.R") 
source("DataSetsQualities.R") 
source("LinearPlots.R") 
source("Clustering.R") 

createHugeMatrixFromFunction <- TRUE 
newMethod <- TRUE  

normalize <- FALSE 
w <- 0.1 
throwMissingValuesVariable <- FALSE 
thresholdForEmptyAlgsResults <- 250 

makeIndividualParetoFrontForEveryDataSet <- FALSE 

clustersFromAnalysis <- TRUE 
plotAlgorithmsIndex <- TRUE 

# create a matrix from a function or from a script 
if (createHugeMatrixFromFunction) { 
    # compute matrix with original method or from separately imputed values   
    if (newMethod) { 
        # get the data from a local directory and clean it 
        cleanedEvaluations <- getCleanMeasures(p.throwMissingValues = throwMissingValuesVariable, p.emptyAlgResults = thresholdForEmptyAlgsResults) 
        
        hugeMatrix <- createHugeMatrixFromImputedMeasures(evaluations = cleanedEvaluations, p.w = w, p.normalize = normalize, p.newFormula = TRUE)  
    } else {
        # get the data from a local directory 
        evaluations <- loadEvaluations() 
        
        hugeMatrix <- createHugeMatrix(originData = evaluations, splitFactor = "task_id", p.w = w, p.normalize = normalize, p.matrixEmptyValuesThrow = throwMissingValuesVariable) 
        hugeMatrix <- cleanEmptyColumns(hugeMatrix, thresholdForEmptyAlgsResults)  
        hugeMatrix <- imputeEmptyVals(hugeMatrix)
    } 
} else {
    source("HugeMatrix.R")        
}  

# image with Pareto front for every data set
if (makeIndividualParetoFrontForEveryDataSet) {
    source("DataPresent.R") 
}

# perform SVD analysis and decompose a matrix
decomposedMatrix <- makeSVDanalysis(resMatrix = hugeMatrix, p.numLatent = 10) 
resMatrixDecomposed_d <- decomposedMatrix$u
resMatrixDecomposed_a <- t(decomposedMatrix$v) 

# get clusters for data sets with kMeans algorithm
clustersProperties <- getOriginalClusters() 
if (clustersFromAnalysis) {
    clusters <- as.integer(clustersProperties$dsClusters)  
} else {
    clusters <- getkMeansClusters(p.matrix = resMatrixDecomposed_d, p.numClusters = clustersProperties$numDataSetsOverall) 
}  
table(clusters) 

# create plots for clusters of data sets  
makePlotsClusters <- FALSE 
if (makePlotsClusters) {
    additionalProcessing()     
} 

# create files with a Pareto front for every data set and every cluster 
selectedAlgorithms <- makeClustering(p.clusters = clusters, p.plots = TRUE, p.simplify = FALSE, p.compareProperties = FALSE, p.includeAdditionalThing = TRUE)   
aggregatedAUC <- data.frame(tapply(selectedAlgorithms$AUC, selectedAlgorithms$Algorithm, mean)) 
aggregatedAUC <- data.frame(aggregatedAUC[order(as.numeric(aggregatedAUC[, 1]), decreasing = TRUE), ])   
selectedAlgorithms <- rownames(aggregatedAUC) 

manualOrder <- FALSE 
if (manualOrder) {
    # manual change in the order of algorithms for new plots 
    selectedAlgorithms <- c(selectedAlgorithms[1], selectedAlgorithms[15:17], selectedAlgorithms[2:8], selectedAlgorithms[18], selectedAlgorithms[9:14]) 
}

# split the main matrix to clusters 
hugeMatrixFirst <- hugeMatrix[clusters == 1, ] 
hugeMatrixSecond <- hugeMatrix[clusters == 2, ] 

# identify a limited number of data sets for detailed analysis 
selectedDataSets <- getMedoids(p.matrix = hugeMatrixFirst, p.numMedoids = 9)  
selectedDataSets <- c(selectedDataSets, getMedoids(p.matrix = hugeMatrixSecond, p.numMedoids = 1)) 

# set permanent structure for data sets for analysis 
lowDimSets <- getRows(p.selectedAlgorithms = selectedAlgorithms, p.cleanedEvaluations = cleanedEvaluations) 
aMatrix <- hugeMatrix[lowDimSets, selectedAlgorithms]  
decomposedAlgsMatrix <- makeSVDanalysis(resMatrix = aMatrix, p.numLatent = 5) 
resAlgsMatrixDecomposed_d <- decomposedAlgsMatrix$u 
resAlgsMatrixDecomposed_a <- t(decomposedAlgsMatrix$v) 

# create heatmaps and place them in files 
dendrograms <- createHeatMapsComplex(
    p.matrix = aMatrix, p.dataSetsDecomposed = resAlgsMatrixDecomposed_d,
    p.algorithmsDecomposed = resAlgsMatrixDecomposed_a, p.distfunction = 'euclidean',
    p.savePath = 'plots/', p.numOfIntervals = 50, p.fixColumns = TRUE) 
 
# perform PCA analysis and put plots in files
pcaPlots(p.matrix = resMatrixDecomposed_d, p.clusters = clusters, p.alternative = FALSE) 

# study visualization received with t-SNE method and resistance against noise
tsnePlot(p.matrix = resMatrixDecomposed_d, p.names = rownames(hugeMatrix), noiseLevel = 0, 
         p.clusters = clusters) 

# create a plot with index w and Frugality score values
if (plotAlgorithmsIndex) {
    makeLinearPLots(p.algorithms = selectedAlgorithms, p.datasets = selectedDataSets, p.normalize = normalize, w.start = 0, w.finish = 1, p.cleanedEvaluations = cleanedEvaluations)  
} 
   
