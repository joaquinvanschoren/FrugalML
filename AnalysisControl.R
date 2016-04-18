source("SciCl.R") 
source("HugeMatrixFunctions.R") 
source("GetCleanMeasuresFromSource.R") 

createHugeMatrixFromFunction <- TRUE 
newMethod <- TRUE  
     
makeIndividualParetoFrontForEveryDataSet <- FALSE 
makeParetoFrontForClusters <- FALSE 

normalize <- FALSE 
w <- 0.7 
throwMissingValuesVariable <- FALSE 
thresholdForEmptyAlgsResults <- 250 

# create a matrix from a function or from a script 
if (createHugeMatrixFromFunction) { 
    # compute matrix with original method or from separately imputed values   
    if (newMethod) { 
        # get the data from a local directory and clean it 
        cleanedEvaluations <- getCleanMeasures(p.throwMissingValues = throwMissingValuesVariable, p.emptyAlgResults = thresholdForEmptyAlgsResults) 
        
        hugeMatrix <- createHugeMatrixFromImputedMeasures(evaluations = cleanedEvaluations, p.w = w, p.normalize = normalize) 
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

# create files with a Pareto front for every data set and every cluster
if (makeParetoFrontForClusters) {
    source("Clustering.R")
} 


