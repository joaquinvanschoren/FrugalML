library(SpatioTemporal) 
library(cluster) 

source("HugeMatrixFunctions.R") 
source("GetCleanMeasuresFromSource.R") 

makeLinearPLots <- function(p.algorithms, p.datasets, p.normalize, 
                            p.printFast = TRUE, w.start, w.finish, 
                            p.cleanedEvaluations) {
    
    wOptions <- seq(w.start, w.finish, 0.1) 

    hugeMatrixSequence <- c() 
    
    for (w in wOptions) { 
        nextMatrix <- createHugeMatrixFromImputedMeasures(evaluations = p.cleanedEvaluations, p.w = w, p.normalize = p.normalize) 

        nextMatrix <- nextMatrix[p.datasets, p.algorithms]  
        hugeMatrixSequence <- append(hugeMatrixSequence, nextMatrix) 
    } 
    
    dfValues <- as.data.frame(matrix(sapply(hugeMatrixSequence, mean), ncol = length(p.algorithms), byrow = TRUE))  
    colnames(dfValues) <- p.algorithms 
    rownames(dfValues) <- as.character(wOptions) 
    
    lw <- length(wOptions) 
    png(filename= paste("plots/linearPlots_", lw, ".png", sep = ""), width = 800, height = 600)   
    
    if (p.printFast) { 
        algQn <- length(p.algorithms) 
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
    
    dev.off() 
}
 

 