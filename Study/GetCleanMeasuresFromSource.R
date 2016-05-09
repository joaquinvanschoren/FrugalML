source("HugeMatrixFunctions.R")
source("SciCl.R") 


computeAUC <- function(dataSet) {
    dataSet <-
        setNames(
            dataSet, c(
                "task_id", "dataset", "algo", "error_message", "accuracy", "auroc", "training_millis", "testing_millis", "confusion_matrix"
            )
        )
    
    # change type from factor to numeric and remove NA values
    dataSet$auroc <- as.numeric(as.character(dataSet$auroc))
    dataSet <- dataSet[!is.na(dataSet$auroc),]
    
    # store highest value for AUC in this particular data set
    highestAUC <- max(dataSet$auroc)
    
    if (highestAUC <= 0 | highestAUC > 1 | is.na(highestAUC)) { 
        return (NA)        
    } 
    
    # filter columns and save AUC, training time and name of algorithm
    dataSet <-
        data.frame(dataSet$auroc, dataSet$algo)
    smallX <-
        setNames(dataSet, c("AUC", "Algorithm"))
    
    return (smallX)
} 

cleanTime <- function(dataMatrix, dataSet, valOfAlgs) { 
    # receive name
    xName <- as.character(dataSet[1, 2]) 
    
    # compute new values and check if data correct 
    cleanData <- computeTime(dataSet) 
    if (is.na(cleanData)) {
        return (NA) 
    } 
    
    # create a vector of values for this set
    processedValues <- t(data.frame(rep(NA, valOfAlgs)))
    colnames(processedValues) <- colnames(dataMatrix)
    rownames(processedValues) <- xName
    
    # update value for each algorithm and data set
    for (algValue in 1:nrow(cleanData)) {
        algName <- shortNameAlgorithmIndividual(as.character(cleanData[algValue, 2])) 
        algIndex <-
            grep(pattern = algName, x = colnames(processedValues), ignore.case = TRUE)
        processedValues[1, algIndex] <- cleanData[algValue, 1] 
    }
    
    return (processedValues) 
} 

computeTime <- function(dataSet) {
    dataSet <-
        setNames(
            dataSet, c(
                "task_id", "dataset", "algo", "error_message", "accuracy", "auroc", "training_millis", "testing_millis", "confusion_matrix"
            )
        )
    
    # convert time variable to numbers
    dataSet$training_millis <-
        as.numeric(as.character(dataSet$training_millis))
    dataSet$testing_millis <-
        as.numeric(as.character(dataSet$testing_millis))
    
    # check for missing or negative values
    dataSet <- dataSet[!is.na(dataSet$training_millis),]
    dataSet$training_millis <- abs(dataSet$training_millis)
    
    dataSet <- dataSet[!is.na(dataSet$testing_millis),]
    dataSet$testing_millis <- abs(dataSet$testing_millis) 
    
    # increase time up to 1 ms for lower values 
    dataSet$training_millis[dataSet$training_millis < 1] <- 1 
    dataSet$testing_millis[dataSet$testing_millis < 1] <- 1 
    
    # caclulate combined time for each algorithm
    dataSet$combineTime <-
        dataSet$training_millis + dataSet$testing_millis
    
    # find the lowest combined time in this data set
    lowestTime <- min(dataSet$combineTime) 
    
    if (is.na(lowestTime)) {
        return (NA)  
    } 
    
    # filter columns and save AUC, training time and name of algorithm
    dataSet <-
        data.frame(dataSet$combineTime, dataSet$algo)
    smallX <-
        setNames(dataSet, c("CombineTime", "Algorithm"))
    
    return (smallX)
} 

getCleanMeasures <- function(p.throwMissingValues = TRUE, p.emptyAlgResults) { 
    evaluations <- loadEvaluations() 
    splitFactor <- 'task_id' 
    separate_evaluations <- split(evaluations, evaluations[splitFactor]) 
    
    # find all algrothms from all results
    algorithms <- data.frame(levels(evaluations$algo))
    
    # prepare matrix with filling in the names of algorithms as column names
    matrixGeneric <- data.frame(t(algorithms)) 
    colnames(matrixGeneric) <- algorithms[1:103, 1]
    matrixGeneric <- matrixGeneric[-1,]
    
    # store the number of algorithms in all items
    numOfAlgs <- dim(matrixGeneric)[2] 
    
    # prepare names of algorithms for processing
    colnames(algorithms) <- "algorithm" 
    algorithms$algorithm <- as.character(algorithms$algorithm) 
    
    # create short names
    for (i in 1:numOfAlgs) {
        algFullName <- as.character(algorithms[i, 1])
        algorithms[i, 1] <- shortNameAlgorithmIndividual(algFullName) 
    }
    algorithms <- data.frame(t(algorithms)) 
    
    # change from factor to character
    algorithms[] <- lapply(algorithms, as.character)
    
    # replace long names with short names
    colnames(matrixGeneric) <- algorithms[1,]
    
    # calculate the number of all sets in files
    quantityDataSets <- length(separate_evaluations)
    
    cleanAUC <- function(dataMatrix, dataSet, valOfAlgs) { 
        # receive name
        xName <- as.character(dataSet[1, 2]) 
        
        # compute new values and check if data correct 
        cleanData <- computeAUC(dataSet) 
        if (is.na(cleanData)) {
            return (NA) 
        } 
        
        # create a vector of values for this set
        processedValues <- t(data.frame(rep(NA, valOfAlgs)))
        colnames(processedValues) <- colnames(dataMatrix)
        rownames(processedValues) <- xName
        
        # update value for each algorithm and data set
        for (algValue in 1:nrow(cleanData)) {
            algName <- shortNameAlgorithmIndividual(as.character(cleanData[algValue, 2])) 
            algIndex <-
                grep(pattern = algName, x = colnames(processedValues), ignore.case = TRUE)
            processedValues[1, algIndex] <- cleanData[algValue, 1] 
        } 
        
        return (processedValues) 
    } 
    
    matrixAUC <- matrixGeneric 
    matrixTime <- matrixGeneric 
    
    for (i in 1:quantityDataSets) {
        # make a copy of set
        dataSet <- as.data.frame(separate_evaluations[i])
        
        pAUC <- cleanAUC(dataMatrix = matrixAUC, dataSet = dataSet, valOfAlgs = numOfAlgs) 
        pTime <- cleanTime(dataMatrix = matrixTime, dataSet = dataSet, valOfAlgs = numOfAlgs) 
        
        # add results to the matrix
        if (!is.na(pAUC) & !is.na(pTime)) {
            matrixAUC <- rbind(matrixAUC, pAUC) 
            matrixTime <- rbind(matrixTime, pTime) 
        } else { 
            print(paste("The next data set is skipped ", as.character(dataSet[1, 2]), ", id = ", i, sep = "")) 
        } 
    } 

    matrixAUC <- cleanEmptyColumns(matrixAUC, p.emptyAlgResults) 
    matrixTime <- cleanEmptyColumns(matrixTime, p.emptyAlgResults) 
    
    if (ncol(matrixAUC) != ncol(matrixTime)) {
        print("please check the number of columns") 
    }

    if (p.throwMissingValues) {
        matrixAUC <- throwMissingValues(dataMatrix = matrixAUC, p.matrixEmptyValuesThrow = TRUE) 
        matrixTime <- throwMissingValues(dataMatrix = matrixTime, p.matrixEmptyValuesThrow = TRUE)         
    } 

    return (list(matrixAUC, matrixTime)) 
} 
