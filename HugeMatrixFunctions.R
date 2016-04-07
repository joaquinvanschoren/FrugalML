
createHugeMatrix <- function(originData, splitFactor, p.w = 0.1, p.normalize = TRUE, p.matrixEmptyValuesThrow = FALSE) {
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
        algorithms[i, 1] <- shortNameAlgorithmIndividual(algFullName)
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
        
        processedDataSet <- frugalityScoreData(hugeMatrix, dataSet, p.w = p.w, p.normalize <- p.normalize, valOfAlgs = numOfAlgs) 
        
        # add results to the matrix
        if (!is.na(processedDataSet)) {
            hugeMatrix <- rbind(hugeMatrix, processedDataSet[[2]])         
        } else { 
            print(paste("The next data set is skipped ", as.character(dataSet[1, 2]), ", id = ", i, sep = "")) 
        } 
    } 
    
    # explore the number of missing values for data sets
    dataSetMissingValues <- data.frame()
    for (i in 1:quantityDataSets) {
        sumMissing <- sum(is.na(hugeMatrix[i,]))
        dataSetMissingValues <-
            rbind(dataSetMissingValues, data.frame(rownames(hugeMatrix)[i], sumMissing))
    }
    dataSetMissingValues <-
        dataSetMissingValues[order(dataSetMissingValues$sumMissing, decreasing = TRUE),]
    
    # find the number of missing values for algorithms
    algsMissingValues <- data.frame()
    for (i in 1:numOfAlgs) {
        sumMissing <- sum(is.na(hugeMatrix[, i]))
        algsMissingValues <-
            rbind(algsMissingValues, data.frame(colnames(hugeMatrix)[i], sumMissing))
    }
    algsMissingValues <-
        algsMissingValues[order(algsMissingValues$sumMissing, decreasing = TRUE),]
    
    matrixEmptyValuesThrow <- p.matrixEmptyValuesThrow  
    
    if (matrixEmptyValuesThrow) { 
        deleteAllEmpty <- FALSE 
        if (deleteAllEmpty) {
            # find all algorithms with missing values
            topAlgorithmsMissingValues <-
                algsMissingValues[algsMissingValues[, 2] > 0,]        
        } else {
            # select algorithms with more than half of missing values
            limit <- 10 
            topAlgorithmsMissingValues <-
                algsMissingValues[algsMissingValues[, 2] >= limit,] 
        }
        
        # delete them from a matrix
        hugeMatrix <-
            hugeMatrix[,!colnames(hugeMatrix) %in% topAlgorithmsMissingValues[, 1]] 
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

frugalityScoreData <- function(dataMatrix, dataSet, p.w, p.normalize = TRUE, replaceMissingValues = FALSE, valOfAlgs) { 
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
        algName <- shortNameAlgorithmIndividual(as.character(cleanData[algValue, 3])) 
        algIndex <-
            grep(pattern = algName, x = colnames(processedValues), ignore.case = TRUE)
        processedValues[1, algIndex] <-
            cleanData[algValue, 1] - p.w * log10(1 + cleanData[algValue, 2])
    }

    # scale all values with respect to the best result   
     if (p.normalize) {
        bestFrugalValue <- max(processedValues, na.rm = TRUE) 
        lowestFrugalScore <- min(processedValues, na.rm = TRUE) 
        processedValues <- (processedValues - lowestFrugalScore) / (bestFrugalValue - lowestFrugalScore)         
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

    if (highestAUC <= 0 | highestAUC > 1 | is.na(highestAUC)) { 
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
    
    if (is.na(lowestTime)) {
        return (NA)  
    }
    
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

shortNameAlgorithmIndividual <- function(algOriginalName) {
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
    
    if (shortName == "1105_lazy.IBk") {
        if (algOriginalName == "1105_weka.classifiers.lazy.IBk -- -K 1 -W 0 -A \\weka.core.neighboursearch.LinearNNSearch -A \\\\\\weka.core.EuclideanDistance -R first-last\\\\\\\\") { 
            shortName <- "1105_lazy.IBk.K1" 
        } else if (algOriginalName == "1105_weka.classifiers.lazy.IBk -- -K 3 -W 0 -A \\weka.core.neighboursearch.LinearNNSearch -A \\\\\\weka.core.EuclideanDistance -R first-last\\\\\\\\") { 
            shortName <- "1105_lazy.IBk.K3" 
        } else if (algOriginalName == "1105_weka.classifiers.lazy.IBk -- -K 5 -W 0 -A \\weka.core.neighboursearch.LinearNNSearch -A \\\\\\weka.core.EuclideanDistance -R first-last\\\\\\\\") { 
            shortName <- "1105_lazy.IBk.K5" 
        }
    } else if (shortName == "1112_functions.LibLINEAR") {
        if (algOriginalName == "1112_weka.classifiers.functions.LibLINEAR -- -S 1 -C 1.0 -E 0.001 -B 1.0") {
            shortName <- "1112_LibLINEAR.E0.001" 
        } else if (algOriginalName == "1112_weka.classifiers.functions.LibLINEAR -- -S 1 -C 1.0 -E 0.01 -B 1.0") {
            shortName <- "1112_LibLINEAR.E0.01" 
        }
    } else if (shortName == "1165_bayes.AveragedNDependenceEstimators.A1DE") {
            shortName <- "1165_bayes.A1DE" 
    } else if (shortName == "1166_bayes.AveragedNDependenceEstimators.A2DE") {
        shortName <- "1166_bayes.A2DE" 
    }
    else if (shortName == "1182_meta.AdaBoostM1") {
            if (algOriginalName == "1182_weka.classifiers.meta.AdaBoostM1 -- -P 100 -S 1 -I 10 -W weka.classifiers.trees.DecisionStump") {
                shortName <- "1182_AdaBoostM1.I10" 
            } else if (algOriginalName == "1182_weka.classifiers.meta.AdaBoostM1 -- -P 100 -S 1 -I 160 -W weka.classifiers.trees.DecisionStump") {
                shortName <- "1182_AdaBoostM1.I160" 
            } else if (algOriginalName == "1182_weka.classifiers.meta.AdaBoostM1 -- -P 100 -S 1 -I 20 -W weka.classifiers.trees.DecisionStump") {
                shortName <- "1182_AdaBoostM1.I20" 
            } else if (algOriginalName == "1182_weka.classifiers.meta.AdaBoostM1 -- -P 100 -S 1 -I 40 -W weka.classifiers.trees.DecisionStump") {
                shortName <- "1182_AdaBoostM1.I40" 
            } else if (algOriginalName == "1182_weka.classifiers.meta.AdaBoostM1 -- -P 100 -S 1 -I 80 -W weka.classifiers.trees.DecisionStump") {
                shortName <- "1182_AdaBoostM1.I80" 
            }
    } else if (shortName == "1185_meta.Bagging") { 
        if (algOriginalName == "1185_weka.classifiers.meta.Bagging -- -P 100 -S 1 -num-slots 1 -I 10 -W weka.classifiers.trees.REPTree -- -M 2 -V 0.001 -N 3 -S 1 -L -1 -I 0.0") {
            shortName <- "1185_Bagging.I10" 
        } else if (algOriginalName == "1185_weka.classifiers.meta.Bagging -- -P 100 -S 1 -num-slots 1 -I 160 -W weka.classifiers.trees.REPTree -- -M 2 -V 0.001 -N 3 -S 1 -L -1 -I 0.0") {
            shortName <- "1185_Bagging.I160" 
        } else if (algOriginalName == "1185_weka.classifiers.meta.Bagging -- -P 100 -S 1 -num-slots 1 -I 20 -W weka.classifiers.trees.REPTree -- -M 2 -V 0.001 -N 3 -S 1 -L -1 -I 0.0") {
            shortName <- "1185_Bagging.I20" 
        } else if (algOriginalName == "1185_weka.classifiers.meta.Bagging -- -P 100 -S 1 -num-slots 1 -I 40 -W weka.classifiers.trees.REPTree -- -M 2 -V 0.001 -N 3 -S 1 -L -1 -I 0.0") {
            shortName <- "1185_Bagging.I40" 
        } else if (algOriginalName == "1185_weka.classifiers.meta.Bagging -- -P 100 -S 1 -num-slots 1 -I 80 -W weka.classifiers.trees.REPTree -- -M 2 -V 0.001 -N 3 -S 1 -L -1 -I 0.0") {
            shortName <- "1185_.Bagging.I80" 
        }
    } else if (shortName == "1191_meta.LogitBoost") {
            if (algOriginalName ==  "1191_weka.classifiers.meta.LogitBoost -- -P 100 -L -1.7976931348623157E308 -H 1.0 -Z 3.0 -O 1 -E 1 -S 1 -I 10 -W weka.classifiers.trees.DecisionStump") {
                shortName <- "1191_LogitBoost.I10" 
            } else if (algOriginalName ==  "1191_weka.classifiers.meta.LogitBoost -- -P 100 -L -1.7976931348623157E308 -H 1.0 -Z 3.0 -O 1 -E 1 -S 1 -I 160 -W weka.classifiers.trees.DecisionStump") {
                shortName <- "1191_LogitBoost.I160" 
            } else if (algOriginalName ==  "1191_weka.classifiers.meta.LogitBoost -- -P 100 -L -1.7976931348623157E308 -H 1.0 -Z 3.0 -O 1 -E 1 -S 1 -I 20 -W weka.classifiers.trees.DecisionStump") {
                shortName <- "1191_LogitBoost.I20" 
            } else if (algOriginalName ==  "1191_weka.classifiers.meta.LogitBoost -- -P 100 -L -1.7976931348623157E308 -H 1.0 -Z 3.0 -O 1 -E 1 -S 1 -I 40 -W weka.classifiers.trees.DecisionStump") {
                shortName <- "1191_LogitBoost.I0" 
            } else if (algOriginalName ==  "1191_weka.classifiers.meta.LogitBoost -- -P 100 -L -1.7976931348623157E308 -H 1.0 -Z 3.0 -O 1 -E 1 -S 1 -I 80 -W weka.classifiers.trees.DecisionStump") {
                shortName <- "1191_LogitBoost.I80" 
            }   
    } else if (shortName == "1192_meta.MultiBoostAB") {
        if (algOriginalName == "1192_weka.classifiers.meta.MultiBoostAB -- -C 3 -P 100 -S 1 -I 10 -W weka.classifiers.trees.DecisionStump") { 
            shortName <- "1192_MultiBoostAB.I10" 
        } else if (algOriginalName == "1192_weka.classifiers.meta.MultiBoostAB -- -C 3 -P 100 -S 1 -I 20 -W weka.classifiers.trees.DecisionStump") { 
            shortName <- "1192_MultiBoostAB.I160" 
        } else if (algOriginalName == "1192_weka.classifiers.meta.MultiBoostAB -- -C 3 -P 100 -S 1 -I 160 -W weka.classifiers.trees.DecisionStump") { 
            shortName <- "1192_MultiBoostAB.I20" 
        } else if (algOriginalName == "1192_weka.classifiers.meta.MultiBoostAB -- -C 3 -P 100 -S 1 -I 40 -W weka.classifiers.trees.DecisionStump") { 
            shortName <- "1192_MultiBoostAB.I40" 
        } else if (algOriginalName == "1192_weka.classifiers.meta.MultiBoostAB -- -C 3 -P 100 -S 1 -I 80 -W weka.classifiers.trees.DecisionStump") { 
            shortName <- "1192_MultiBoostAB.I80" 
        } 
    } else if (shortName == "1194_meta.RandomSubSpace") {
        if (algOriginalName == "1194_weka.classifiers.meta.RandomSubSpace -- -P 0.5 -S 1 -num-slots 1 -I 10 -W weka.classifiers.trees.REPTree -- -M 2 -V 0.001 -N 3 -S 1 -L -1 -I 0.0") { 
            shortName <- "1194_RandomSubSpace.I10" 
        } else if (algOriginalName == "1194_weka.classifiers.meta.RandomSubSpace -- -P 0.5 -S 1 -num-slots 1 -I 160 -W weka.classifiers.trees.REPTree -- -M 2 -V 0.001 -N 3 -S 1 -L -1 -I 0.0") { 
            shortName <- "1194_RandomSubSpace.I160" 
        } else if (algOriginalName == "1194_weka.classifiers.meta.RandomSubSpace -- -P 0.5 -S 1 -num-slots 1 -I 20 -W weka.classifiers.trees.REPTree -- -M 2 -V 0.001 -N 3 -S 1 -L -1 -I 0.0") { 
            shortName <- "1194_RandomSubSpace.I20" 
        } else if (algOriginalName == "1194_weka.classifiers.meta.RandomSubSpace -- -P 0.5 -S 1 -num-slots 1 -I 40 -W weka.classifiers.trees.REPTree -- -M 2 -V 0.001 -N 3 -S 1 -L -1 -I 0.0") { 
            shortName <- "1194_RandomSubSpace.I40" 
        } else if (algOriginalName == "1194_weka.classifiers.meta.RandomSubSpace -- -P 0.5 -S 1 -num-slots 1 -I 80 -W weka.classifiers.trees.REPTree -- -M 2 -V 0.001 -N 3 -S 1 -L -1 -I 0.0") { 
            shortName <- "1194_RandomSubSpace.I80" 
        }
    }  else if (shortName == "1195_meta.RealAdaBoost") {
        if (algOriginalName == "1195_weka.classifiers.meta.RealAdaBoost -- -P 100 -H 1.0 -S 1 -I 10 -W weka.classifiers.trees.DecisionStump") { 
            shortName <- "1195_meta.RealAdaBoost.I10" 
        } else if (algOriginalName == "1195_weka.classifiers.meta.RealAdaBoost -- -P 100 -H 1.0 -S 1 -I 160 -W weka.classifiers.trees.DecisionStump") { 
            shortName <- "1195_meta.RealAdaBoost.I160" 
        } else if (algOriginalName == "1195_weka.classifiers.meta.RealAdaBoost -- -P 100 -H 1.0 -S 1 -I 20 -W weka.classifiers.trees.DecisionStump") { 
            shortName <- "1195_meta.RealAdaBoost.I20" 
        } else if (algOriginalName == "1195_weka.classifiers.meta.RealAdaBoost -- -P 100 -H 1.0 -S 1 -I 40 -W weka.classifiers.trees.DecisionStump") { 
            shortName <- "1195_meta.RealAdaBoost.I40" 
        } else if (algOriginalName == "1195_weka.classifiers.meta.RealAdaBoost -- -P 100 -H 1.0 -S 1 -I 80 -W weka.classifiers.trees.DecisionStump") { 
            shortName <- "1195_meta.RealAdaBoost.I80" 
        }
    } else if (shortName == "1196_meta.RotationForest") {
        if (algOriginalName == "1196_weka.classifiers.meta.RotationForest -- -G 3 -H 3 -P 50 -F \\weka.filters.unsupervised.attribute.PrincipalComponents -R 1.0 -A 5 -M -1\\ -S 1 -num-slots 1 -I 10 -W weka.classifiers.trees.J48 -- -C 0.25 -M 2") {
            shortName <- "1196_RotationForest.I10" 
        } else if (algOriginalName == "1196_weka.classifiers.meta.RotationForest -- -G 3 -H 3 -P 50 -F \\weka.filters.unsupervised.attribute.PrincipalComponents -R 1.0 -A 5 -M -1\\ -S 1 -num-slots 1 -I 160 -W weka.classifiers.trees.J48 -- -C 0.25 -M 2") {
            shortName <- "1196_RotationForest.I160" 
        } else if (algOriginalName == "1196_weka.classifiers.meta.RotationForest -- -G 3 -H 3 -P 50 -F \\weka.filters.unsupervised.attribute.PrincipalComponents -R 1.0 -A 5 -M -1\\ -S 1 -num-slots 1 -I 20 -W weka.classifiers.trees.J48 -- -C 0.25 -M 2") {
            shortName <- "1196_RotationForest.I20" 
        } else if (algOriginalName == "1196_weka.classifiers.meta.RotationForest -- -G 3 -H 3 -P 50 -F \\weka.filters.unsupervised.attribute.PrincipalComponents -R 1.0 -A 5 -M -1\\ -S 1 -num-slots 1 -I 40 -W weka.classifiers.trees.J48 -- -C 0.25 -M 2") {
            shortName <- "1196_RotationForest.I40" 
        } else if (algOriginalName == "1196_weka.classifiers.meta.RotationForest -- -G 3 -H 3 -P 50 -F \\weka.filters.unsupervised.attribute.PrincipalComponents -R 1.0 -A 5 -M -1\\ -S 1 -num-slots 1 -I 80 -W weka.classifiers.trees.J48 -- -C 0.25 -M 2") {
            shortName <- "1196_RotationForest.I80" 
        }
    } 
    
    
    return (shortName)
}

# function for use advanced version of heatmap plot  
drawPlot <- function(p.matrix, fileName, 
                     width = 3200, height = 2400, dendrogramType, 
                     p.distfunction = 'minkowski', 
                     decomposed = FALSE, 
                     breakLen, p.Rowv = NULL, p.Colv = NULL, 
                     p.savePath = "plots/", 
                     p.cellNote = FALSE, p.keysize = 0.3, 
                     p.lmat = NULL, p.lhei = NULL, p.lwid = NULL, 
                     p.cexRow = 0.9, p.cexCol = 2, p.margins = c(50, 50)) {

    # define source palette for scaling 
    getPalette = colorRampPalette(brewer.pal(9, "YlOrRd")) 

    # calculate number of intervals for a legend 
    if (decomposed) {
        breaks_s <- seq(quantile(p.matrix, 0.01, na.rm = TRUE), quantile(p.matrix, 0.99, na.rm = TRUE), length.out = breakLen + 1) 
        # breaks_s <- c(-1, breaks_s) 
    } else {
        breaks_s <- seq(0, 1, length.out = breakLen + 1) 
    }
    
    # create a file 
    png(filename = paste(p.savePath, fileName, sep = ""), width = width, height = height) 
    
    # check whether it is necessary to draw a vakue within a cell 
    if (p.cellNote) { 
        hm2res <- heatmap.2(as.matrix(p.matrix), 
                            breaks = breaks_s, col = getPalette(breakLen), keysize = p.keysize, 
                            Colv = p.Colv, Rowv = p.Rowv, density.info = "none", trace = "none", dendrogram = c(dendrogramType), 
                            symm=F,symkey=F,symbreaks=T, 
                            scale="none",  distfun = function(x) dist(x,method = p.distfunction), 
                            cellnote = as.matrix(round(p.matrix, 2)),  
                            cexRow = p.cexRow, cexCol = p.cexCol, 
                            margins = p.margins, 
                            lmat = p.lmat,
                            lhei = p.lhei, 
                            lwid = p.lwid) 
    } else {
        hm2res <- heatmap.2(as.matrix(p.matrix), 
                            breaks = breaks_s, col = getPalette(breakLen), keysize = p.keysize, 
                            Colv = p.Colv, Rowv = p.Rowv, density.info = "none", trace = "none", dendrogram = c(dendrogramType), 
                            symm=F,symkey=F,symbreaks=T, 
                            scale="none",  distfun = function(x) dist(x,method = p.distfunction), 
                            cexRow = p.cexRow, cexCol = p.cexCol, 
                            margins = p.margins, 
                            lmat = p.lmat,
                            lhei = p.lhei, 
                            lwid = p.lwid) 
    } 
    dev.off() 
    
    return (hm2res) 
} 

compareMatrices <- function(a, b) {
    sod <- c() 
    for (i in 1:nrow(a)) {
        brc <- setequal(a[i, ], b[i, ])
        sod <- c(sod, brc)
    } 
    matches <- sum(sod) 
    
    if (matches == nrow(a)) {
        return (TRUE) 
    } else {
        return (FALSE) 
    }
} 

loadEvaluations <- function() {
    evaluations <- read.csv("data/openml_evaluations_all.csv") 
    
    return (evaluations) 
}

 