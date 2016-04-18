library(SpatioTemporal) 
library(cluster) 

source("HugeMatrixFunctions.R")  
 
printFast <- TRUE 
wOptions <- seq(0.0, 1.5, 0.1) 

# results of algorithms
evaluations <- read.csv("data/openml_evaluations_all.csv") 
hugeMatrix <- createHugeMatrix(evaluations, "task_id", p.w = 0.1, p.normalize = FALSE, p.matrixEmptyValuesThrow = TRUE) 

# compute missed values
hugeMatrixImputed <- SVDmiss(X = hugeMatrix, niter = 25, ncomp = dim(hugeMatrix)[2], conv.reldiff = 1E-3) 
hugeMatrix <- hugeMatrixImputed$Xfill 

sAlgorithm1 <- grep("RandomTree", colnames(hugeMatrix)) 
sAlgorithm2 <- grep("BayesNet", colnames(hugeMatrix)) 
sAlgorithm3 <- grep("HyperPipes", colnames(hugeMatrix)) 
sAlgorithm4 <- grep("VFI", colnames(hugeMatrix)) 
sAlgorithm5 <- grep("REPTree", colnames(hugeMatrix)) 
sAlgorithm6 <- grep("LogitBoost", colnames(hugeMatrix))[1]  
sAlgorithm7 <- grep("Dagging", colnames(hugeMatrix)) 
sAlgorithm8 <- grep("RotationForest", colnames(hugeMatrix))[1] 
sAlgorithm9 <- grep("IBk.K1", colnames(hugeMatrix)) 
sAlgorithm10 <- grep("RandomForest", colnames(hugeMatrix))  

algorithms <- colnames(hugeMatrix)[c(sAlgorithm1, sAlgorithm2, sAlgorithm3, sAlgorithm4, sAlgorithm5, 
                                     sAlgorithm6, sAlgorithm7, sAlgorithm8, sAlgorithm9, sAlgorithm10)] 

# use pam clustering and find central elelements   
pamClusters <- pam(x = hugeMatrix, 10) 
dataSets <- rownames(pamClusters$medoids)  

hugeMatrixSequence <- c() 

for (w in wOptions) { 
    nextMatrix <- createHugeMatrix(evaluations, "task_id", p.w = w, p.normalize = FALSE, p.matrixEmptyValuesThrow = TRUE) 

    # matrix has empty values 
    hasEmptyValues <- sum(is.na(nextMatrix)) != 0 
    
    if (hasEmptyValues) {
        # compute missed values
        nextMatrixImputed <- SVDmiss(X = nextMatrix, niter = 25, ncomp = dim(nextMatrix)[2], conv.reldiff = 1E-3) 
        nextMatrix <- nextMatrixImputed$Xfill  
    } 

    nextMatrix <- nextMatrix[dataSets, algorithms]  
    hugeMatrixSequence <- append(hugeMatrixSequence, nextMatrix) 
} 

dfValues <- as.data.frame(matrix(sapply(hugeMatrixSequence, mean), ncol = length(algorithms), byrow = TRUE))  
colnames(dfValues) <- algorithms 
rownames(dfValues) <- as.character(wOptions) 
 
if (printFast) {
    algQn <- length(algorithms) 
    matplot(dfValues, type = c("b"), pch=1, col = 1:algQn, ylim = c(0, 1), ylab = "Frugality score", xlab = "Index w", axes = F) #plot  
    legend("topright", legend = colnames(dfValues)[1:algQn] , col=1:algQn, cex = 0.55, pch=1) # optional legend  
    axis(2) 
    axis(side = 1, at = 1:nrow(dfValues), labels = rownames(dfValues)) 
    } else { 
    dfValues$w <- wOptions  
    
    p <- ggplot() + 
        geom_line(data = dfValues, aes(x = dfValues$w, y = dfValues[, 1], color = colnames(dfValues)[1])) + 
        geom_line(data = dfValues, aes(x = dfValues$w, y = dfValues[, 2], color = colnames(dfValues)[2])) +  
        geom_line(data = dfValues, aes(x = dfValues$w, y = dfValues[, 3], color = colnames(dfValues)[3])) + 
        geom_line(data = dfValues, aes(x = dfValues$w, y = dfValues[, 4], color = colnames(dfValues)[4])) + 
        geom_line(data = dfValues, aes(x = dfValues$w, y = dfValues[, 5], color = colnames(dfValues)[5])) + 
        geom_line(data = dfValues, aes(x = dfValues$w, y = dfValues[, 6], color = colnames(dfValues)[6])) + 
        geom_line(data = dfValues, aes(x = dfValues$w, y = dfValues[, 1], color = colnames(dfValues)[7])) + 
        geom_line(data = dfValues, aes(x = dfValues$w, y = dfValues[, 1], color = colnames(dfValues)[8])) + 
        geom_line(data = dfValues, aes(x = dfValues$w, y = dfValues[, 1], color = colnames(dfValues)[9])) + 
        geom_line(data = dfValues, aes(x = dfValues$w, y = dfValues[, 1], color = colnames(dfValues)[10])) + 
        xlab('Index w') + 
        ylab('Frugality score') + 
        labs(color = 'Algorithms') + 
        ylim(0, 1) 
    
    print(p) 
}
   

 