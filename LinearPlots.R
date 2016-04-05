
library(SpatioTemporal) 

algorithms <- sample(colnames(hugeMatrix), 5) 

dataSets <- rownames(medos) 

hugeMatrixSequence <- c() 

for (w in seq(0.1, 0.5, 0.1)) { 
    nextMatrix <- createHugeMatrix(evaluations, "task_id") 

    nextMatrix <- nextMatrix[dataSets, algorithms]  
    
    # matrix has empty values 
    hasEmptyValues <- sum(is.na(nextMatrix)) != 0 
    
    if (hasEmptyValues) {
        # compute missed values
        hugeMatrixImputed <- SVDmiss(X = hugeMatrix, niter = 25, ncomp = dim(hugeMatrix)[2], conv.reldiff = 1E-3) 
        
        hugeMatrix <- hugeMatrixImputed$Xfill 
    } 
    
    hugeMatrixSequence <- append(hugeMatrixSequence, nextMatrix) 
} 


dfValues <- as.data.frame(matrix(sapply(hugeMatrixSequence, mean), ncol = 5)) 

colnames(dfValues) <- algorithms 

rownames(dfValues) <- as.character(seq(0.1, 0.5, 0.1)) 

dfValues$w <- seq(0.1, 0.5, 0.1) 
  

for (i in 1:5) {
    plot(dfValues$w, dfValues[, i]) 
    
    lml <- lm(dfValues$w ~ dfValues[, i]) 
    
    abline(lml) 
} 


matplot(dfValues, type = c("b"),pch=1,col = 1:5) #plot
legend("topright", legend = colnames(dfValues)[1:5] , col=1:5, cex = 0.5, pch=1) # optional legend 

ggplot(dfValues, aes(w)) + 
    geom_line(aes(y = colnames(dfValues)[1], colour = "var0")) + 
    geom_line(aes(y = colnames(dfValues)[2], colour = "var1"))


for (i in 1:nrow(medos)) {
    sbx <- rownames(medos)[i] 

    for (j in 1:517) { 
        ds <- as.data.frame(separate_evaluations[j]) 
        xName <- as.character(ds[1, 2]) 
        
        if (xName == sbx) {
            
        }
    }
    

    
    pData <- computeTieAUC() 
    
} 

 