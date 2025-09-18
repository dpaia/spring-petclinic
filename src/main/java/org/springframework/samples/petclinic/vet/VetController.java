/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.vet;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

/**
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Ken Krebs
 * @author Arjen Poutsma
 */
@Controller
class VetController {

	private static final int PAGE_SIZE = 5;

	private final VetService vetService;

	public VetController(VetService vetService) {
		this.vetService = vetService;
	}

	@GetMapping("/vets.html")
	public Mono<String> showVetList(@RequestParam(defaultValue = "1") int page, Model model) {
		// Here we are returning an object of type 'Vets' rather than a collection of Vet
		// objects so it is simpler for Object-Xml mapping
		return findPaginatedReactive(page).map(tuple -> addPaginationModel(page, tuple.getT2(), tuple.getT1(), model));
	}

	private String addPaginationModel(int page, Long totalItems, List<Vet> paginated, Model model) {
		model.addAttribute("totalPages", (int) Math.ceil((double) totalItems / PAGE_SIZE));
		model.addAttribute("currentPage", page);
		model.addAttribute("totalItems", totalItems);
		model.addAttribute("listVets", paginated);
		return "vets/vetList";
	}

	private Mono<Tuple2<List<Vet>, Long>> findPaginatedReactive(int page) {
		Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE);
		return vetService.findAllPaginatedReactive(pageable);
	}

	@GetMapping({ "/vets" })
	@ResponseBody
	public Mono<Vets> showResourcesVetList() {
		// Here we are returning an object of type 'Vets' rather than a collection of Vet
		// objects so it is simpler for JSon/Object mapping
		return vetService.getVetsAsListReactive();
	}

}
