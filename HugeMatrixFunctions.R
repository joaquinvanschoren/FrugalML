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
    source("HugeMatrixFunctions.R") 
    # change from factor to character
    algorithms[] <- lapply(algorithms, as.character)
    
    # replace long names with short names
    colnames(hugeMatrix) <- algorithms[1,]
    
    # calculate the number of all sets in files
    quantityDataSets <- length(separate_evaluations) 

    for (i in 1:quantityDataSets) {
        # make a copy of set
        dataSet <- as.data.frame(separate_evaluations[i])
        
        processedDataSet <- frugalityScoreData(hugeMatrix, dataSet, valOfAlgs = numOfAlgs) 

        # add results to the matrix
        if (!is.na(processedDataSet)) {
            hugeMatrix <- rbind(hugeMatrix, processedDataSet[[2]])         
        } 
    } 
    return (hugeMatrix)
}
   
originalScoreData <- function(dataMatrix, dataSet, replaceMissingValues = FALSE, valOfAlgs) { 
    ## standard version with the original frulgality score 
    
    # receive name
    xName <- as.character(dataSet[1, 2]) 

    # compute new values and check if data correct 
    cleanData <- computeTimeAUC(dataSet) 
    if (is.na(cleanData)) {
        return (NA) 
    } 
    
    # create a vector of values for this set
    processedValues <- t(data.frame(rep(NA, valOfAlgs)))
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

frugalityScoreData <- function(dataMatrix, dataSet, w = 0.1, replaceMissingValues = FALSE, valOfAlgs) { 
    ## standard version with the original frulgality score 
    
    # receive name
    xName <- as.character(dataSet[1, 2]) 
 
    # compute new values and check if data correct 
    cleanData <- computeTimeAUC(dataSet) 
    if (is.na(cleanData)) {
        return (NA) 
    } 
    
    # create a vector of values for this set
    processedValues <- t(data.frame(rep(NA, valOfAlgs)))
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
            cleanData[algValue, 1] - w * log(1 + cleanData[algValue, 2])
    }

    # scale all values with respect to the best result   
    bestFrugalValue <- max(processedValues, na.rm = TRUE) 
    lowestFrugalScore <- min(processedValues, na.rm = TRUE) 
    processedValues <- (processedValues - lowestFrugalScore) / (bestFrugalValue - lowestFrugalScore) 

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

    if (highestAUC <= 0 | highestAUC > 1) { 
        return (NA)        
    } 
    
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

# function for use advanced version of heatmap plot  
drawPlot <- function(p.matrix, fileName, 
                     width = 6400, height = 4800, dendrogramType, 
                     p.distfunction = 'minkowski', 
                     decomposed = FALSE, 
                     breakLen, p.Rowv = NULL, p.Colv = NULL, 
                     p.cellNote = FALSE, p.keysize = 0.3, 
                     p.lmat = NULL, p.lhei = NULL, p.lwid = NULL, 
                     p.cexRow = 0.9, p.cexCol = 2, p.margins = c(50, 50)) {
    
    # calculate number of intervals for a legend 
    
    if (decomposed) {
        breaks_s <- seq(quantile(p.matrix, 0.05, na.rm = TRUE), quantile(p.matrix, 0.95, na.rm = TRUE), length.out = breakLen) 
    } else {
        breaks_s <- seq(0, quantile(p.matrix, 0.95, na.rm = TRUE), length.out = breakLen - 1) 
        breaks_s <- c(-1, breaks_s) 
    }
    
    # create a file 
    png(filename = paste(savePath, fileName, sep = ""), width = width, height = height) 
    
    # check whether it is necessary to draw a vakue within a cell 
    if (p.cellNote) { 
        hm2res <- heatmap.2(as.matrix(p.matrix), 
                            breaks = breaks_s, col = brewer.pal(8, "YlOrRd"), keysize = p.keysize, 
                            Colv = p.Colv, Rowv = p.Rowv, density.info = "none", trace = "none", dendrogram = c(dendrogramType), 
                            symm=F,symkey=F,symbreaks=T, 
                            scale="none",  distfun = function(x) dist(x,method = distfunction), 
                            cellnote = as.matrix(round(p.matrix, 2)),  
                            cexRow = p.cexRow, cexCol = p.cexCol, 
                            margins = p.margins, 
                            lmat = p.lmat,
                            lhei = p.lhei, 
                            lwid = p.lwid) 
    } else {
        hm2res <- heatmap.2(as.matrix(p.matrix), 
                            breaks = breaks_s, col = brewer.pal(8, "YlOrRd"), keysize = p.keysize, 
                            Colv = p.Colv, Rowv = p.Rowv, density.info = "none", trace = "none", dendrogram = c(dendrogramType), 
                            symm=F,symkey=F,symbreaks=T, 
                            scale="none",  distfun = function(x) dist(x,method = distfunction), 
                            cexRow = p.cexRow, cexCol = p.cexCol, 
                            margins = p.margins, 
                            lmat = p.lmat,
                            lhei = p.lhei, 
                            lwid = p.lwid) 
    } 
    dev.off() 
    
    return (hm2res) 
} 

