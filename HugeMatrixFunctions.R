createHugeMatrix <- function(originData, splitFactor) {
    separate_evaluations <- split(originData, originData[splitFactor])
    
    # find all algrothms from all results
    algorithms <- data.frame(levels(originData$algo))
    
    # prepare matrix with filling in the names of algorithms as column names
    hugeMatrix <- data.frame(t(algorithms))
    colnames(hugeMatrix) <- algorithms[1:nrow(algorithms), 1] 
    hugeMatrix <- hugeMatrix[-1,]
    
    # store the number of algorithms in all items
    numOfAlgs <- dim(hugeMatrix)[2]
    
    # prepare names of algorithms for processing
    colnames(algorithms) <- "algorithm"
    algorithms$algorithm <- as.character(algorithms$algorithm)
    
    # create short names
    for (i in 1:numOfAlgs) {
        algFullName <- as.character(algorithms[i, 1])
        algorithms[i, 1] <- shortNameAlgorithm(algFullName)
    }
    algorithms <- data.frame(t(algorithms))
    
    # change from factor to character
    algorithms[] <- lapply(algorithms, as.character)
    
    # replace long names with short names
    colnames(hugeMatrix) <- algorithms[1,]
    
    # calculate the number of all sets in files
    quantityDataSets <- length(separate_evaluations) 

    for (i in 1:quantityDataSets) {
        # make a copy of set
        dataSet <- as.data.frame(separate_evaluations[i])
        
        processedDataSet <- originalScoreData(hugeMatrix, dataSet)

        # add results to the matrix
        hugeMatrix <- rbind(hugeMatrix, processedDataSet[[2]]) 
    }
    return (hugeMatrix)
}

originalScoreData <- function(dataMatrix, dataSet, replaceMissingValues = FALSE) { 
    ## standard version with the original frulgality score 
    
    # receive name
    xName <- as.character(dataSet[1, 2]) 
    
    cleanData <- computeTimeAUC(dataSet)
    
    # create a vector of values for this set
    processedValues <- t(data.frame(rep(NA, numOfAlgs)))
    colnames(processedValues) <- colnames(dataMatrix)
    rownames(processedValues) <- xName
    
    # update value for each algorithm and data set
    for (algValue in 1:nrow(cleanData)) {
        algName <- as.character(cleanData[algValue, 3])
        algIndex <-
            grep(
                pattern = substr(algName, 0, 5), x = colnames(processedValues), ignore.case = TRUE
            )
        processedValues[1, algIndex] <-
            cleanData[algValue, 1] - 0.1 * log(1 + cleanData[algValue, 2])
    }

    # calculate the quntity of missing values for a data set     
    sumMissing <- sum(is.na(processedValues)) 
    missingData <- data.frame(xName, sumMissing) 
    
    # replace all NA values with negative value
    if (replaceMissingValues) {
        processedValues[is.na(processedValues)] <- -1 
    } 
        
    return (list(missingData, processedValues)) 
} 

frugalityScoreData <- function(dataMatrix, dataSet, replaceMissingValues = FALSE) { 
    ## standard version with the original frulgality score 
    
    # receive name
    xName <- as.character(dataSet[1, 2]) 
    
    cleanData <- computeTimeAUC(dataSet)
    
    # create a vector of values for this set
    processedValues <- t(data.frame(rep(NA, numOfAlgs)))
    colnames(processedValues) <- colnames(dataMatrix)
    rownames(processedValues) <- xName
    
    # update value for each algorithm and data set
    for (algValue in 1:nrow(cleanData)) {
        algName <- as.character(cleanData[algValue, 3])
        algIndex <-
            grep(
                pattern = substr(algName, 0, 5), x = colnames(processedValues), ignore.case = TRUE
            )
        processedValues[1, algIndex] <-
            cleanData[algValue, 1] - 0.1 * log(1 + cleanData[algValue, 2])
    }

    # scale all values with respect to the best result   
    bestFrugalValue <- max(processedValues, na.rm = TRUE) 
    processedValues <- processedValues / bestFrugalValue 

    # calculate the quntity of missing values for a data set     
    sumMissing <- sum(is.na(processedValues)) 
    missingData <- data.frame(xName, sumMissing) 
    
    # replace all NA values with negative value
    if (replaceMissingValues) {
        processedValues[is.na(processedValues)] <- -1 
    } 
    
    return (list(missingData, processedValues)) 
} 

computeTimeAUC <- function(dataSet) {
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
    
    # caclulate combined time for each algorithm
    dataSet$combineTime <-
        dataSet$training_millis + dataSet$testing_millis
    
    # find the lowest combined time in this data set
    lowestTime <- min(dataSet$combineTime + 1)
    
    # filter columns and save AUC, training time and name of algorithm
    dataSet <-
        data.frame(dataSet$auroc, dataSet$combineTime, dataSet$algo)
    smallX <-
        setNames(dataSet, c("AUC", "CombineTime", "Algorithm"))
    smallX <-
        smallX[order(smallX$AUC,smallX$CombineTime,decreasing = TRUE),]
    
    return (smallX)
}

shortNameAlgorithm <- function(algOriginalName) {
    posOfSpace <- gregexpr(pattern = ' ', algOriginalName)[[1]][1]
    
    if (posOfSpace > 0) {
        shortName <-
            paste(
                substr(algOriginalName, 0, 5), substr(algOriginalName, 23, posOfSpace - 1), sep = ""
            )
    } else {
        shortName <-
            paste(substr(algOriginalName, 0, 5), substr(algOriginalName, 23, nchar(algOriginalName)), sep = "")
    }
    
    return (shortName)
}
