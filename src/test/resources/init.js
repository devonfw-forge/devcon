function(arg1, arg2){

	if (arg1 === undefined){
		this.getOutput().showMessage("NO arguments");
		return null;
	} else {
		this.getOutput().showMessage("Arguments: " + arg1 + " and " + arg2);
		return arg1 + arg2;
	}
}
