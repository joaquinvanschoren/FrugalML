library(soobench)
library(mlr)
library(mlrMBO)
obj.fun = rastrigin_function(1)

par.set = makeNumericParamSet(len = 1, id = "x", lower = lower_bounds(obj.fun), upper = upper_bounds(obj.fun))
learner = makeLearner("regr.km", predict.type = "se", covtype = "matern3_2")
control = makeMBOControl(
    propose.points = 1,
    iters = 5,
    infill.crit = "ei"
)

result = mbo(makeMBOFunction(obj.fun), par.set = par.set, learner = learner, control = control) 