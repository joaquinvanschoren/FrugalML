source("SciCl.R")  
source("HugeMatrixFunctions.R") 
source("GetCleanMeasuresFromSource.R") 
source("DataSetsQualities.R") 

oldwd <- getwd()

# set your own directory 
setwd("/home/mikhail/Desktop/GitProjects/FrugalML/Study") 

makeClustering <- function(p.clusters, p.plots = FALSE, p.simplify = FALSE, p.compareProperties = FALSE, p.includeAdditionalThing = FALSE) { 
    # control for grouping different values of hyper parameters to a one value  
    simplify <- p.simplify  

    # choose algorithms for detailed evaluation 
    compareProperties <- p.compareProperties 
 
    # include algorithm for detailed analysis 
    includeAdditionalThing <- p.includeAdditionalThing 
       
    # create images for Pareto Front 
    showParetoFront <- TRUE 
       
    # leave or delete outliers in results 
    filterOutlierValues <- TRUE 

    # present results for an example algorithm 
    removeSomeAlgorithms <- TRUE 
    
    # load original data for consistency
    evaluations <- loadEvaluations() 
    separate_evaluations <- split(evaluations, evaluations$task_id) 
    separate_evaluations <- cleanMissingItems(separate_evaluations)  

    allMeasures <- length(p.clusters) 
    
    selectedAlgorithms <- data.frame() 
    
    # script contains values for time and AUC that are not normalized, need to rewrite if use to create plots 
    for (i in 1: max(p.clusters)) {
        combinedParetoFrontsinCluster <- data.frame() 
        
        combinedAllResultsinCluster <- data.frame() 
        
        setCount <- 0
        
        for (j in 1: allMeasures) {
            if (p.clusters[j] == i) {
                
                setCount <- setCount + 1 
                
                # make a copy of set
                x <- as.data.frame(separate_evaluations[j])
                
                smallX <- computeTimeAUC(x) 
                
                smallX$CombineTime <- log10(smallX$CombineTime) 
                
                smallX$AUC <- smallX$AUC * -1
                
                combinedAllResultsinCluster <- rbind(combinedAllResultsinCluster, smallX) 
            }
        } 
        
        # change variable from Pareto front to all values   
        combinedParetoFrontsinCluster <- combinedAllResultsinCluster
        
        # remove NaN values    
        combinedParetoFrontsinCluster <- combinedParetoFrontsinCluster[!is.na(combinedParetoFrontsinCluster$AUC), ] 
        
        # change type of the column with algorithms to character 
        combinedParetoFrontsinCluster$Algorithm <- as.character(combinedParetoFrontsinCluster$Algorithm) 
        
        # compute how many times an algorithm appeared on Pareto front in a cluster 
        frequencies <- data.frame(table(as.character(combinedParetoFrontsinCluster$Algorithm)))  
        
        # calculate average values for each algorithm 
        aggregatedAUC <- data.frame(tapply(combinedParetoFrontsinCluster$AUC, combinedParetoFrontsinCluster$Algorithm, mean)) 
        aggregatedTime <- data.frame(tapply(combinedParetoFrontsinCluster$CombineTime, combinedParetoFrontsinCluster$Algorithm, mean)) 
        
        aggregatedValues <- cbind(aggregatedAUC, aggregatedTime)
        aggregatedValues <- setNames(aggregatedValues, c("AUC", "CombineTime")) 
        aggregatedValues$Algorithm <- rownames(aggregatedValues)  
        
        # additional check for empty values    
        aggregatedValues <- aggregatedValues[!is.na(aggregatedValues$AUC), ] 

        for (j in 1: nrow(aggregatedValues)) {
            if (unlist(gregexpr(pattern ='--', aggregatedValues[j, 3], fixed = TRUE)) != -1)
            {
                aggregatedValues[j, 4] <- substr(aggregatedValues[j, 3], unlist(gregexpr(pattern ='.', aggregatedValues[j, 3], fixed = TRUE))[3] + 1, unlist(gregexpr(pattern ='--', aggregatedValues[j, 3], fixed = TRUE))[1] - 2) 
            } else {
                aggregatedValues[j, 4] <- substr(aggregatedValues[j, 3], unlist(gregexpr(pattern ='.', aggregatedValues[j, 3], fixed = TRUE))[3] + 1, nchar(aggregatedValues[j, 3])) 
            }
            aggregatedValues[j, 5] <- substr(aggregatedValues[j, 3], 0, 4) 
        } 
        
        # process names of algorithms with a common template 
        aggregatedValues$ShortAlgorithm <- sapply(aggregatedValues$Algorithm, function(x) shortNameAlgorithmIndividual(x)) 
        
        # combine results and frequencies for algorithms 
        aggregatedValues$Frequency <- as.numeric(frequencies$Freq) 
        
        # filter algorithms that appear in less than half observations 
        if (filterOutlierValues) {
            aggregatedValues <- aggregatedValues[aggregatedValues$Frequency >= setCount / 2, ]         
        } 
        
        # remove an example for other algorithms  
        if (removeSomeAlgorithms) {
            zrIndex <- grep("ZeroR", rownames(aggregatedValues))[1]  
            fcIndex <- grep("FilteredClassif", rownames(aggregatedValues))
            aggregatedValues <- aggregatedValues[-c(zrIndex, fcIndex), ] 
        }  
        
        if (simplify == TRUE) {  
            aggregatedAUC <- data.frame(tapply(aggregatedValues$AUC, aggregatedValues$V4, mean)) 
            aggregatedTime <- data.frame(tapply(aggregatedValues$CombineTime, aggregatedValues$V4, mean)) 
            aggregatedFrequency <- data.frame(tapply(aggregatedValues$Frequency, aggregatedValues$V4, sum)) 
            
            aggregatedValues <- cbind(aggregatedAUC, aggregatedTime, aggregatedFrequency) 
            aggregatedValues <- setNames(aggregatedValues, c("AUC", "CombineTime", "Frequency")) 
            aggregatedValues$Algorithm <- rownames(aggregatedValues)  
            
            aggregatedValues <- aggregatedValues[!is.na(aggregatedValues$AUC), ] 
        } else { 
            aggregatedAUC <- data.frame(tapply(aggregatedValues$AUC, aggregatedValues$ShortAlgorithm, mean)) 
            aggregatedTime <- data.frame(tapply(aggregatedValues$CombineTime, aggregatedValues$ShortAlgorithm, mean)) 
            aggregatedFrequency <- data.frame(tapply(aggregatedValues$Frequency, aggregatedValues$ShortAlgorithm, sum)) 
            
            aggregatedValues <- cbind(aggregatedAUC, aggregatedTime, aggregatedFrequency) 
            aggregatedValues <- setNames(aggregatedValues, c("AUC", "CombineTime", "Frequency")) 
            aggregatedValues$Algorithm <- rownames(aggregatedValues)  
            
            aggregatedValues <- aggregatedValues[!is.na(aggregatedValues$AUC), ] 
        }

        if(compareProperties) { 
            pickSpecialAlgorithms <- c(grep("1182", aggregatedValues[, 4]), grep("1195", aggregatedValues[, 4]))  
            aggregatedValues <- aggregatedValues[pickSpecialAlgorithms, ] 
        } 
                
        aggregatedValues <- aggregatedValues[order(-aggregatedValues$AUC, aggregatedValues$CombineTime, decreasing = TRUE), ] 
        
        # calculate Pareto front
        paretoFront = aggregatedValues[which(!duplicated(cummin(aggregatedValues$CombineTime))),] 
        
        if (includeAdditionalThing & i == 1) { 
            abIndex <- grep("AdaBoostM1.I10", rownames(aggregatedValues)) 
            paretoFront <- rbind(paretoFront, aggregatedValues[abIndex, ])  
        }  
        
        selectedAlgorithms <- rbind(selectedAlgorithms, cbind(i, paretoFront)) 
        
        if (p.plots) { 
            if (showParetoFront == TRUE) { 
                numOfAlgs <- nrow(paretoFront) 
                png(filename= paste("plots/cluster_Pareto_", i, ".png", sep = ""), width = 800, height = 600)    
                plot(paretoFront[,1:2], col = rainbow(numOfAlgs) , xaxt = "n", xlim=c(min(paretoFront$AUC), max(paretoFront$AUC)), ylim=c(0, max(paretoFront$CombineTime)), pch = 20, cex = 2.9, main = paste("Size of cluster num ", i, " is ", setCount)) 
                axis(1, at = seq(min(paretoFront$AUC), max(paretoFront$AUC), abs(min(paretoFront$AUC) - max(paretoFront$AUC))/5),  labels = -1 * round(seq(min(paretoFront$AUC), max(paretoFront$AUC), abs(min(paretoFront$AUC) - max(paretoFront$AUC))/5), 2))   

                if (simplify == TRUE) { 
                    legend("topright", legend = paste(paretoFront[, 3], paretoFront[, 4], sep = "_"), col =  rainbow(numOfAlgs) , pch = 20, lty = 1, cex = 1.1, pt.cex = 1.8) 
                    text(paretoFront[,1:2], labels = paste(paretoFront[, 3], paretoFront[, 4], sep = "_"), cex = 0.7, pos = 3 ) 
                } else { 
                    legend("topright", legend = paste(paretoFront[, 3], paretoFront[, 4], sep = "_"), col =  rainbow(numOfAlgs) , pch = 20, lty = 1, cex = 0.8, pt.cex = 1.8) 
                    text(paretoFront[,1:2], labels = paste(paretoFront[, 3], paretoFront[, 4], sep = "_"), cex = 0.7, pos = 3 ) 
                }
                dev.off() 
            } 
            
            sizeOfAlgs <- nrow(aggregatedValues) 
            png(filename= paste("plots/cluster_", i, ".png", sep = ""), width = 1600, height = 1200)     
            plot(aggregatedValues[,1:2], col = rainbow(sizeOfAlgs) , xaxt = "n", xlim=c(min(aggregatedValues$AUC), max(aggregatedValues$AUC)), ylim=c(0, max(aggregatedValues$CombineTime)), pch = 20, cex = 2.9, main = paste("Size of cluster num ", i, " is ", setCount)) 
            axis(1, at = seq(min(aggregatedValues$AUC), max(aggregatedValues$AUC), abs(min(aggregatedValues$AUC) - max(aggregatedValues$AUC))/5),  labels = -1 * round(seq(min(aggregatedValues$AUC), max(aggregatedValues$AUC), abs(min(aggregatedValues$AUC) - max(aggregatedValues$AUC))/5), 2)) 
            
            if (simplify == TRUE) {
                legend("topright", legend = paste(aggregatedValues[, 3], aggregatedValues[, 4], sep = "_"), col =  rainbow(sizeOfAlgs) , pch = 20, lty = 1, cex = 0.9, pt.cex = 1.8) 
                text(aggregatedValues[,1:2], labels = paste(aggregatedValues[, 3], aggregatedValues[, 4], sep = "_"), cex = 0.7, pos = 3 ) 
            } else { 
                legend("topright", legend = paste(aggregatedValues[, 3], aggregatedValues[, 4], sep = "_"), col =  rainbow(sizeOfAlgs), pch = 20, lty = 1, cex = 0.7, pt.cex = 1.8) 
                text(aggregatedValues[,1:2], labels = paste(aggregatedValues[, 3], aggregatedValues[, 4], sep = "_"), cex = 0.7, pos = 3 )     
            }
            dev.off() 
        }
    } 
    
    return (selectedAlgorithms) 
}
 
setwd(oldwd) 
 