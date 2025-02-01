module db_lab(
	input clk
);
wire [4:1]db_dOUT;
wire db_reset;
wire db_sel;

lab(
	.clk(clk),
	.sel(db_sel),
	.reset(db_reset),
	.dOUT(db_dOUT)
);

MY_ISSPE ISSPE_lab(
	.source({db_reset, db_sel}),
	.probe(db_dOUT),
	.source_clk(clk),
);
endmodule